package com.face.recognition;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.opencv.core.Core;

import java.security.MessageDigest;
import java.util.Base64;

public class SegundoTeste {
    private static final String SOURCE_IMAGE_PATH_IN = "img/in";
    private static final String OPENCV_DATA_PATH = "data/faces";

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String cascadePath = OPENCV_DATA_PATH + "/haarcascade_frontalface_default.xml";
        CascadeClassifier faceCascade = new CascadeClassifier(cascadePath);
    }

//    public static void main(String[] args) {
//        // Carregar bibliotecas nativas do OpenCV
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        System.out.println("Welcome to Benezinho Feature Extraction with OpenCV: " + Core.VERSION);
//
//        // Definir o classificador de faces (XML do OpenCV)
//        String cascadePath = OPENCV_DATA_PATH + "/haarcascade_frontalface_default.xml";
//        CascadeClassifier faceCascade = new CascadeClassifier(cascadePath);
//
//        // Carregar imagens
//        Mat angelinaJolie = opencv_imgcodecs.imread(SOURCE_IMAGE_PATH_IN + "/angelina_jolie_1.jpg");
//        Mat angelinaJolie2 = opencv_imgcodecs.imread(SOURCE_IMAGE_PATH_IN + "/brad_pitt_2.jpg");
//
//        // Detectar rostos
//        Rect faceRect1 = detectFace(angelinaJolie, faceCascade);
//        Rect faceRect2 = detectFace(angelinaJolie2, faceCascade);
//
//        if (faceRect1 != null && faceRect2 != null) {
//            Mat face1 = new Mat(angelinaJolie, faceRect1);
//            Mat face2 = new Mat(angelinaJolie2, faceRect2);
//
//            // Convertendo para escala de cinza
//            opencv_imgproc.cvtColor(face1, face1, opencv_imgproc.COLOR_BGR2GRAY);
//            opencv_imgproc.cvtColor(face2, face2, opencv_imgproc.COLOR_BGR2GRAY);
//
//            // Inicializar o LBPHFaceRecognizer para extração de características
//            LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
//
//            // Adicionar faces e rótulos ao treino
//            MatVector images = new MatVector(2);
//            images.put(0, face1);
//            images.put(1, face2);
//
//            Mat labels = new Mat(2, 1, opencv_core.CV_32SC1);
//            labels.ptr(0, 0).putInt(0);
//            labels.ptr(1, 0).putInt(1);
//
//            recognizer.train(images, labels);
//
//
//            // Extrair características das faces
//            int[] label1 = new int[1];
//            double[] confidence1 = new double[1];
//            recognizer.predict(face1, label1, confidence1);
//
//            int[] label2 = new int[1];
//            double[] confidence2 = new double[1];
//            recognizer.predict(face2, label2, confidence2);
//
//            // Comparar as faces usando confiança
//            double similarity = compareConfidence(confidence1[0], confidence2[0]);
//            System.out.println("Similaridade entre os rostos: " + similarity);
//
//            // Gerar hash das características
//            String hash1 = generateHash(face1);
//            String hash2 = generateHash(face2);
//            System.out.println("Hash do rosto 1: " + hash1);
//            System.out.println("Hash do rosto 2: " + hash2);
//        } else {
//            System.out.println("Nenhum rosto detectado em uma ou ambas as imagens.");
//        }
//    }


    private static Rect detectFace(Mat image, CascadeClassifier faceCascade) {
        Mat grayImage = new Mat();
        opencv_imgproc.cvtColor(image, grayImage, opencv_imgproc.COLOR_BGR2GRAY);

        // Detectar faces
        RectVector facesDetected = new RectVector();
        faceCascade.detectMultiScale(grayImage, facesDetected);

        // Verifique se há faces detectadas
        if (facesDetected.size() > 0) {
            return facesDetected.get(0); // Retorna o primeiro rosto detectado
        } else {
            return null; // Nenhum rosto encontrado
        }
    }

    private static Mat extractFeatures(Mat face, LBPHFaceRecognizer recognizer) {
        int[] label = new int[1]; // Vetor para armazenar o rótulo
        double[] confidence = new double[1]; // Vetor para armazenar a confiança

        try {
            // Chamada ao método predict() com os parâmetros corretos
            recognizer.predict(face, label, confidence);

            // Verifique a confiança, uma confiança baixa pode indicar uma boa correspondência
            System.out.println("Rótulo: " + label[0] + ", Confiança: " + confidence[0]);

            return face; // Retorna a face, mas você pode decidir o que retornar com base na lógica de seu aplicativo
        } catch (Exception e) {
            System.err.println("Erro ao extrair características: " + e.getMessage());
            return null;
        }
    }



    private static double compareFeatures(Mat features1, Mat features2) {
        double sum = 0.0;
        for (int i = 0; i < features1.rows(); i++) {
            double diff = features1.ptr(i, 0).getFloat() - features2.ptr(i, 0).getFloat();
            sum += diff * diff;
        }
        return Math.sqrt(sum); // Distância Euclidiana
    }

    private static double compareConfidence(double confidence1, double confidence2) {
        // Defina um limiar para decidir se as faces são similares
        double threshold = 50.0; // Ajuste esse valor conforme necessário

        // Compare a confiança das duas previsões, se a diferença for baixa, as faces são mais similares
        if (Math.abs(confidence1 - confidence2) < threshold) {
            return 1.0; // Faces semelhantes
        } else {
            return 0.0; // Faces diferentes
        }
    }

    private static String generateHash(Mat features) {
        System.out.println("Features: " + features);
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < features.rows(); i++) {
                sb.append(features.ptr(i, 0).getFloat());
            }
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sb.toString().getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash", e);
        }
    }
}
