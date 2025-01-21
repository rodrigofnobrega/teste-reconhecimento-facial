package com.face.recognition;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.*;
import org.bytedeco.opencv.opencv_highgui.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import org.bytedeco.opencv.opencv_objdetect.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_face.*;
import static org.bytedeco.opencv.global.opencv_highgui.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_objdetect.*;


public class App {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        // Load Face Detector
        CascadeClassifier faceDetector = new CascadeClassifier ("data/haarcascades/haarcascade_frontalface_default.xml");

        // Create an instance of Facemark
        FacemarkKazemi facemark = FacemarkKazemi.create();

        // Load landmark detector
        facemark.loadModel("data/face_landmark_model.dat");

        List<String> filePathImages = new ArrayList<>();
        filePathImages.add("img/in/foto.jpg");
        filePathImages.add("img/in/angelina_jolie_1.jpg");
        filePathImages.add("img/in/angelina_jolie_2.jpg");
        filePathImages.add("img/in/brad_pitt_1.jpg");
        filePathImages.add("img/in/brad_pitt_2.jpg");
        filePathImages.add("img/in/jhonny_depp_1.jpg");
        filePathImages.add("img/in/jhonny_depp_2.jpg");


        for (String imagePath : filePathImages) {
            try {
                // Load image
                Mat img = imread(imagePath);
//            Mat img = imread("img/in/foto.jpg");

                // Resize the image to 225x225
                Mat resizedImg = new Mat();
                resize(img, resizedImg, new Size(225, 225));

                // convert to grayscale and equalize histograe for better detection
                Mat gray = new Mat();
                cvtColor(img, gray, COLOR_BGR2GRAY);
                equalizeHist(gray, gray);

                // Find faces on the image
                RectVector faces = new RectVector();
                faceDetector.detectMultiScale(gray, faces);

                System.out.println("Faces detected: " + faces.size());
                // Variable for landmarks.
                // Landmarks for one face is a vector of points
                // There can be more than one face in the image.
                Point2fVectorVector landmarks = new Point2fVectorVector();

                // Run landmark detector
                boolean success = facemark.fit(img, faces, landmarks);

                if (success) {
                    // If successful, render the landmarks on each face
                    for (long i = 0; i < landmarks.size(); i++) {
                        Point2fVector v = landmarks.get(i);
                        drawFacemarks(img, v, Scalar.YELLOW);
                    }
                }

                // Display results
                imshow("Kazemi Facial Landmark", img);
                cvWaitKey(0);
                // Save results
                imwrite("kazemi_landmarks.jpg", img);

                System.out.println(generateFaceHash(landmarks));
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static String generateFaceHash(Point2fVectorVector landmarks) {
        try {
            // Criação de um objeto StringBuilder para armazenar os pontos
            StringBuilder sb = new StringBuilder();

            // Iterando sobre os landmarks e concatenando as coordenadas dos pontos
            for (int i = 0; i < landmarks.size(); i++) {
                Point2fVector points = landmarks.get(i);
                for (int j = 0; j < points.size(); j++) {
                    Point2f point = points.get(j);
                    sb.append(point.x()).append(",").append(point.y()).append(";");
                }
            }

            // Converter o StringBuilder para uma string
            String landmarksString = sb.toString();

            // Gerar o hash (SHA-256) a partir da string de landmarks
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(landmarksString.getBytes());

            // Converter o hash para uma string Base64 (para facilitar a visualização)
            return Base64.getEncoder().encodeToString(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
