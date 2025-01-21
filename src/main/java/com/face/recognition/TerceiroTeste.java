package com.face.recognition;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.opencv.core.Core;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point2fVectorVector;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_face.FacemarkKazemi;
//import com.shaon2016.facelandmarkusingjavacv.util.FileUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;

public class TerceiroTeste {
    private static final String SOURCE_IMAGE_PATH_IN = "img/in";
    private static final String OPENCV_DATA_PATH = "data/faces";

//    private static final String TAG = FaceDetection.class.getSimpleName();

    private static CascadeClassifier faceCascade;
    private static FacemarkKazemi facemark;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        facemark = FacemarkKazemi.create();

        String cascadePath = OPENCV_DATA_PATH + "/haarcascade_frontalface_default.xml";
        facemark.loadModel( "data/face_landmark_model.dat");
        CascadeClassifier faceCascade = new CascadeClassifier(cascadePath);

        String imagePath = SOURCE_IMAGE_PATH_IN + "/angelina_jolie_2.jpg"; // Substitua pelo caminho da sua imagem
        Mat image = imread(imagePath);

        if (image.empty()) {
            System.out.println("Erro ao carregar a imagem!");
            return;
        }

        // Converter para escala de cinza
        Mat grayImage = new Mat();
        cvtColor(image, grayImage, COLOR_BGR2GRAY);

        // Detecta rostos na imagem
        RectVector faces = new RectVector();
        faceCascade.detectMultiScale(grayImage, faces);

        Point2fVectorVector landmark = detectLandmarks(grayImage, faces);
        System.out.println(landmark);
        String faceHash = generateFaceHash(landmark);
        System.out.println(landmark.size());
        System.out.println(faceHash);
//        System.out.println(landmark.getPointer());
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
                    System.out.println("Pontos Endereço: " + point.address());
                    System.out.println("Pontos X: " + point.y());
                    System.out.println("Pontos Y: " + point.x());
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

//    public static void loadFaceDetectionModel(Context context) {
//        File file = new File(C.modelHarcascadePath(context));
//        if (!file.exists()) {
//            FileUtil.copyFileFromAsset(
//                    context, "haarcascade_frontalface_alt2.xml",
//                    C.modelHarcascadePath(context)
//            );
//        }
//
//        faceCascade = new CascadeClassifier(C.modelHarcascadePath(context));
//    }
//
//    public static void loadFaceMarkModel(Context context) {
//        // Caminho do arquivo local para o modelo de marcos faciais
//        String modelPath = context.getFilesDir().getAbsolutePath() + "/face_landmark_model.dat";
//
//        // Verifique se o arquivo de modelo já existe
//        File file = new File(modelPath);
//        if (!file.exists()) {
//            // Se o arquivo não existe, copie-o da pasta assets para o armazenamento local
//            FileUtil.copyFileFromAsset(
//                    context, "face_landmark_model.dat", modelPath
//            );
//        }
//
//        // Inicialize o FacemarkKazemi
//        facemark = FacemarkKazemi.create();
//
//        // Carregue o modelo de marcos faciais
//        facemark.loadModel(modelPath);
//    }

//    public static RectVector detectFaces(Mat mat) {
//        // Convert to grayscale and equalize histograma for better detection
//        Mat grayMat = new Mat();
//        opencv_imgproc.cvtColor(mat, grayMat, opencv_imgproc.COLOR_BGR2GRAY);
//        opencv_imgproc.equalizeHist(grayMat, grayMat);
//
//        // Find faces on the image
//        RectVector faces = new RectVector();
//        faceCascade.detectMultiScale(grayMat, faces);
//
//        Log.d(TAG, "Faces detected: " + faces.size());
//
//        return faces;
//    }

    public static Point2fVectorVector detectLandmarks(Mat mat, RectVector faces) {
        Point2fVectorVector landmarks = new Point2fVectorVector();

        // Run landmark detector
        facemark.fit(mat, faces, landmarks);

        return landmarks;
    }
}
