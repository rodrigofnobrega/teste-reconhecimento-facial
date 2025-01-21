//package com.face.recognition;
//
//import org.bytedeco.opencv.opencv_core.*;
//import org.bytedeco.opencv.opencv_objdetect.*;
//import static org.bytedeco.opencv.global.opencv_imgproc.*;
//
//
//import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
//import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
//import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
//import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
//
//public class PrimeiroTeste {
//
//    public static void main(String[] args) {
//        // Caminho para o modelo Haar Cascade
//        String cascadeFilePath = "src/main/java/com/face/recognition/haarcascade_frontalface_alt.xml";
//
//        // Carrega o classificador Haar
//        CascadeClassifier faceDetector = new CascadeClassifier(cascadeFilePath);
//
//        // Carrega a imagem para análise
//        String imagePath = "src/main/java/com/face/recognition/foto.jpg"; // Substitua pelo caminho da sua imagem
//        Mat image = imread(imagePath);
//
//        if (image.empty()) {
//            System.out.println("Erro ao carregar a imagem!");
//            return;
//        }
//
//        // Converter para escala de cinza
//        Mat grayImage = new Mat();
//        cvtColor(image, grayImage, COLOR_BGR2GRAY);
//
//
//        // Detecta rostos na imagem
//        RectVector faces = new RectVector();
//        faceDetector.detectMultiScale(grayImage, faces);
//
//        // Desenhar retângulos ao redor dos rostos detectados
//        for (int i = 0; i < faces.size(); i++) {
//            Rect face = faces.get(i);
//            rectangle(image, face, new Scalar(0, 255, 0, 0), 2, LINE_8, 0);
//        }
//
//        // Salvar ou exibir a imagem resultante
//        String outputPath = "output.jpg";
//        imwrite(outputPath, image);
//        System.out.println("Detecção completa. Resultado salvo em " + outputPath);
//    }
//}
