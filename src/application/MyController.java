package application;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.sun.prism.paint.Color;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class MyController {
	@FXML
	private ImageView myImageView;
	
	Mat originImage = Imgcodecs.imread("F:/lena1.png",Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
	int size = (int)(originImage.total()*originImage.channels());
	Mat negativeImage, eqImage, noiseImage, smoothImage;
	int[] intensity = new int[256];
	public void showOriginalImage(ActionEvent event){
		myImageView.setFitHeight(originImage.height());
		myImageView.setFitWidth(originImage.width());
		myImageView.setImage(bufferedImage2FXimage(mat2BufferedImage(originImage)));
		System.out.println(originImage);
		//Imgcodecs.imwrite("F:/lena2.png",image);
	}
	// image transpose
	public void showNegativeImage(ActionEvent event){
		negativeImage = originImage.clone();
		System.out.println(negativeImage);
		for (int i = 0; i < negativeImage.rows(); i++){
		    for (int j = 0; j < negativeImage.cols(); j++) {
		        double[] data = negativeImage.get(i, j);
		        if(negativeImage.channels()>1){
			        data[0] = -data[0]+255;
			        data[1] = -data[0]+255;
			        data[2] = -data[0]+255;
		        }else{
		        	data[0] = -data[0]+255;
		        }
		        negativeImage.put(i, j, data);
		    }
		}
		
		//negativeImage.convertTo(negativeImage, -1,1.5,30); OpenCV function to negative image 
		myImageView.setImage(bufferedImage2FXimage(mat2BufferedImage(negativeImage)));
		System.out.println(negativeImage);
		
	}
	//image equalization
	public void equalization(){
		double[] probability = new double[256];
		int[] gray = new int[256];
		for(int i=0; i<intensity.length; i++){
			intensity[i]=0;
		}
		eqImage = originImage.clone();
		double n = eqImage.total();
		for(int r=0; r<eqImage.rows(); r++){
			for(int c=0; c<eqImage.cols(); c++){
				double[] data = eqImage.get(r, c);
				intensity[(int)data[0]]++;
			}
		}
		double flag=0;
		for(int i=0;i<256;i++){
			if(intensity[i]!=0){
				probability[i] = intensity[i]/n + flag;
				flag = probability[i]; 
			}
		}
		for(int i=0; i<256; i++){
			if(probability[i]!=0){
				double temp = probability[i]*255;
				gray[i] = (int)Math.round(temp);
			}
		}
		for(int r=0; r<eqImage.rows(); r++){
			for(int c=0; c<eqImage.cols(); c++){
				double[] data = eqImage.get(r, c);
				if(gray[(int)data[0]]!=0){
					data[0] = gray[(int)data[0]];
				}
				eqImage.put(r,c,data);
			}
		}
		//Imgcodecs.imwrite("F:/lena3.png",eqImage);
		//Imgproc.equalizeHist(eqImage,eqImage); //OpenCV function to equalize image
		myImageView.setImage(bufferedImage2FXimage(mat2BufferedImage(eqImage)));
		System.out.println(eqImage);
		
	}
	//show origin histogram
	public void showHistogram(){
		BufferedImage pic = new BufferedImage(300,300,BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = pic.createGraphics();
		g2d.setPaint(java.awt.Color.BLACK);
		g2d.fillRect(0,0,300,300);
		g2d.setPaint(java.awt.Color.WHITE);
		g2d.drawLine(10, 290, 290, 290);
		g2d.drawLine(10, 290, 10, 5);
		g2d.setPaint(java.awt.Color.GREEN);    
	    int max = 0;  
		for(int i=0; i<256; i++){
			if(intensity[i]>max){
				max=intensity[i];
			}
		}
	    float rate = 260.0f/((float)max);    
	    for(int i=0; i<256; i++) {    
	    	int frequency = (int)(intensity[i]*rate);    
	        g2d.drawLine(10 + i, 290, 10 + i, 290-frequency);    
	    }             
	    myImageView.setImage(bufferedImage2FXimage(pic));
//		eqImage = originImage.clone();    //OpenCV function to calcHist
//		Mat histImage = new Mat();
//		int[] channel = {0};
//		MatOfInt channels = new MatOfInt(channel);
//		int[] histsize = {256};
//		MatOfInt histSize = new MatOfInt(histsize);
//		float[] range = {0,255};
//		MatOfFloat ranges = new MatOfFloat(range);
//		List<Mat> images = new ArrayList<Mat>();
//		images.add(eqImage);
//		Imgproc.calcHist(images, channels, new Mat(), histImage, histSize, ranges);
//		
//		BufferedImage bi = new BufferedImage(300,300,BufferedImage.TYPE_4BYTE_ABGR);
//		Graphics2D g2d = bi.createGraphics();
//		g2d.setPaint(java.awt.Color.BLACK);
//		g2d.fillRect(0, 0, 300, 300);
//		g2d.setPaint(java.awt.Color.WHITE);
//		g2d.drawLine(10, 290, 290, 290);
//		g2d.drawLine(10, 5, 10, 290);
//		g2d.setPaint(java.awt.Color.GREEN);
//		int max = 0;
//		for(int r=0; r<histImage.rows(); r++){
//				double[] temp = histImage.get(r, 0);
//				if(max<(int)temp[0]){
//					max = (int)temp[0];
//				}
//		}
//		float rate = 260f/((float)max);
//		for(int i=0; i<256; i++){
//			double[] temp = histImage.get(i, 0);
//			int frequency = (int)(temp[0] * rate);
//			g2d.drawLine(10+i, 290, 10+i, 290-frequency);
//		}
//		myImageView.setImage(bufferedImage2FXimage(bi));
	}
	//show equalization histogram
	public void showEqhistogram(){
		int[] gray = new int[256];
		for(int r=0; r<eqImage.rows(); r++){
			for(int c=0; c<eqImage.cols(); c++){
				double[] temp = eqImage.get(r, c);
				gray[(int)temp[0]]++;
			}
		}
		BufferedImage pic = new BufferedImage(300,300,BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = pic.createGraphics();
		g2d.setPaint(java.awt.Color.BLACK);
		g2d.fillRect(0,0,300,300);
		g2d.setPaint(java.awt.Color.WHITE);
		g2d.drawLine(10, 290, 290, 290);
		g2d.drawLine(10, 290, 10, 5);
		g2d.setPaint(java.awt.Color.GREEN);    
	    int max = 0;  
		for(int i=0; i<256; i++){
			if(gray[i]>max){
				max=gray[i];
			}
		}
	    float rate = 260.0f/((float)max);    
	    for(int i=0; i<256; i++) {    
	    	int frequency = (int)(gray[i]*rate);    
	        g2d.drawLine(10 + i, 290, 10 + i, 290-frequency);    
	    }             
	    myImageView.setImage(bufferedImage2FXimage(pic));
	}
	
	public void addSaltAndPepperNoise(){
		Mat outImage = originImage.clone();
		double SNR = 0.95;
		int num = (int)(outImage.rows()*outImage.cols()*(1 - SNR));
		for(int i=0; i<num; i++){
			int row = (int)(Math.random()*outImage.rows());
			int col = (int)(Math.random()*outImage.cols());
			double[] temp = outImage.get(row, col);
			double flag = Math.random();
			if(flag > 0.5){
				if(outImage.channels()>1){
					temp[0] = 255;
					temp[1] = 255;
					temp[2] = 255;
				}else{
					temp[0] = 255;
				}
			}else{
				if(outImage.channels()>1){
					temp[0] = 0;
					temp[1] = 0;
					temp[2] = 0;
				}else{
					temp[0] = 0;
				}
			}
			outImage.put(row, col, temp);
		}
		noiseImage = outImage.clone();
		myImageView.setImage(bufferedImage2FXimage(mat2BufferedImage(noiseImage)));
	}
	
	public void addGaussianNoise(){
		int k = 64;
		Mat outImage = new Mat();
		outImage = originImage.clone();
		System.out.println(outImage);
		for(int r=0; r<outImage.rows(); r++){
			for(int c=0; c<outImage.cols(); c++){
				double[] temp = outImage.get(r, c);
				temp[0] = temp[0] + k * getGaussianNoise(0, 0.8);
				outImage.put(r, c, temp);
			}
		}
		noiseImage = outImage.clone();
		myImageView.setImage(bufferedImage2FXimage(mat2BufferedImage(noiseImage)));
	}
	double V1, V2, S;
	int phase = 0;
	public double getGaussianNoise(double mu, double sigma){		    
	    double X;
	    double U1,U2;
	    if ( phase == 0 ) {
	        do {
	            U1 = (double)Math.random();
	            U2 = (double)Math.random();

	            V1 = 2 * U1 - 1;
	            V2 = 2 * U2 - 1;
	            S = V1 * V1 + V2 * V2;
	        } while(S >= 1 || S == 0);

	        X = V1 * Math.sqrt(-2 * Math.log(S) / S);
	    } else{
	        X = V2 * Math.sqrt(-2 * Math.log(S) / S);
	    }
	    phase = 1 - phase;
	    return (mu+sigma*X);
	}
	
	public void blur(){		
		Mat outImage = new Mat();
		outImage = noiseImage.clone();
//		Imgproc.blur(noiseImage, outImage, new Size(5,5));  //opencv function blur
		int rl=3;
		int cl=3;
		for(int r=0; r<=outImage.rows()-rl; r++){
			for(int c=0; c<=outImage.cols()-cl; c++){
				int sum = 0;
				for(int i=0; i<rl; i++){
					for(int j=0; j<cl; j++){
						double[] temp = outImage.get(r+i, c+j);
						sum = sum + (int)temp[0];
					}
				}
				sum = Math.round((sum*1.0f)/(rl*cl));
				int r_point = r+((rl-1)/2);
				int c_point = c+((cl-1)/2);
				double[] temp = outImage.get(r_point, c_point);
				temp[0] = sum;
				outImage.put(r_point, c_point, temp);
			}
		}
		smoothImage = outImage.clone();
		myImageView.setImage(bufferedImage2FXimage(mat2BufferedImage(outImage)));
		
	}
	
	public void medianBlur(){
		Mat outImage = new Mat();
		outImage = noiseImage.clone();
		//Imgproc.medianBlur(noiseImage, outImage, 5);  //opencv function blur
		int rl=3;
		int cl=3;
		ArrayList<Integer> my_list = new ArrayList<Integer>();
		for(int r=0; r<outImage.rows()-rl; r++){
			for(int c=0; c<outImage.cols()-cl; c++){
				for(int i=0; i<rl; i++){
					for(int j=0; j<cl; j++){
						double[] temp = outImage.get(r+i, c+j);
						my_list.add(((int)temp[0]));
					}
				}
				int r_point = r+((rl-1)/2);
				int c_point = c+((cl-1)/2);
				Collections.sort(my_list);
				int num = my_list.get((rl*cl-1)/2);
				double[] temp = outImage.get(r_point, c_point);
				temp[0] = num;
				outImage.put(r_point, c_point, temp);
				my_list.clear();
			}
		}
		smoothImage = outImage.clone();
		myImageView.setImage(bufferedImage2FXimage(mat2BufferedImage(outImage)));
	}
	
	public void gaussianBlur(){
		Mat outImage = new Mat();
		outImage = noiseImage.clone();
//		Imgproc.GaussianBlur(noiseImage, outImage, new Size(3,3), 0.8); //opencv function gaussianblur
		int rl = 3;
		int cl = 3;
		double sigma = 0.8;
		double[][] gaussianWeight = new double[rl][cl];
		gaussianWeight = getGaussianWeight(rl, cl, sigma, gaussianWeight);
		for(int r=0; r<=outImage.rows()-rl; r++){
			for(int c=0; c<=outImage.cols()-cl; c++){
				double sum = 0;
				
				for(int i=0; i<3; i++){
					for(int j=0; j<3; j++){
						double[] temp = outImage.get(r+i, c+j);
						temp[0] = temp[0] * gaussianWeight[i][j];
						sum = sum + temp[0];
					}
				}
				int r_point = r+((rl-1)/2);
				int c_point = c+((cl-1)/2);
				double[] temp = outImage.get(r_point, c_point);
				temp[0] = sum;
				outImage.put(r_point, c_point, temp);
			}
		}
		smoothImage = outImage.clone();
		myImageView.setImage(bufferedImage2FXimage(mat2BufferedImage(outImage)));
	}
	
	public static double[][] getGaussianWeight(int rl, int cl, double sigma, double[][] gaussianWeight){
		double sum = 0;
		double twosigma2 = 2.0f * sigma * sigma;
		double twopisigma2 = Math.PI * twosigma2;
		int r_point = (rl-1)/2;
		int c_point = (cl-1)/2;
		for(int i=0; i<rl; i++){
			for(int j=0; j<cl; j++){
				int x = i - c_point;
				int y = j - r_point;
				int x2 = x * x;
				int y2 = y * y;
				int x2addy2 = x2 + y2;
				double g = Math.exp((-x2addy2)/(twosigma2))/twopisigma2;
				sum = sum + g;
				gaussianWeight[i][j] = g;
			}
		}
		for(int i=0; i<rl; i++){
			for(int j=0; j<cl; j++){
				
				gaussianWeight[i][j] = gaussianWeight[i][j]/sum;	
				System.out.print(gaussianWeight[i][j]+" ");
			}
			System.out.println();
		}
		return gaussianWeight;
	}
	
	public void laplacian(){
		Mat outImage = new Mat();
		outImage = originImage.clone();
		for(int r=0; r<outImage.rows()-2; r++){
			for(int c=0; c<outImage.cols()-2; c++){
				double[] temp = outImage.get(r+1, c+1);
				double[] temp1 = outImage.get(r, c+1);
				double[] temp2 = outImage.get(r+2, c+1);
				double[] temp3 = outImage.get(r+1, c);
				double[] temp4 = outImage.get(r+1, c+2);
				double sum = 4*temp[0]-temp1[0]-temp2[0]-temp3[0]-temp4[0];
				temp[0] = sum;
				outImage.put(r, c, temp);
			}
		}
		myImageView.setImage(bufferedImage2FXimage(mat2BufferedImage(outImage)));
	}
	
	public void roberts(){
		Mat outImage = new Mat();
		outImage = originImage.clone();
		for(int r=0; r<outImage.rows()-1; r++){
			for(int c=0; c<outImage.cols()-1; c++){
				double[] temp = outImage.get(r, c);
				double[] temp1 = outImage.get(r+1, c+1);
				double[] temp2 = outImage.get(r+1, c);
				double[] temp3 = outImage.get(r, c+1);
				double sum = Math.abs(temp1[0]-temp[0])+Math.abs(temp2[0]-temp3[0]);
				temp[0] = sum;
				outImage.put(r, c, temp);
			}
		}
		myImageView.setImage(bufferedImage2FXimage(mat2BufferedImage(outImage)));
	}
	
	public void sobel(){
		Mat outImage = new Mat();
		outImage = originImage.clone();
		for(int r=0; r<outImage.rows()-2; r++){
			for(int c=0; c<outImage.cols()-2; c++){
				double[] temp = outImage.get(r+1, c+1);
				double[] temp1 = outImage.get(r, c);
				double[] temp2 = outImage.get(r, c+1);
				double[] temp3 = outImage.get(r, c+2);
				double[] temp4 = outImage.get(r+1, c);
				double[] temp6 = outImage.get(r+1, c+2);
				double[] temp7 = outImage.get(r+2, c);
				double[] temp8 = outImage.get(r+2, c+1);
				double[] temp9 = outImage.get(r+2, c+2);
				double sum1 = Math.abs((temp7[0]+2*temp8[0]+temp9[0])-(temp1[0]+2*temp2[0]+temp3[0]));
				double sum2 = Math.abs((temp3[0]+2*temp6[0]+temp9[0])-(temp1[0]+2*temp4[0]+temp7[0]));
				double sum = sum1 + sum2;
				temp[0] = sum;
				outImage.put(r, c, temp);
			}
		}
		myImageView.setImage(bufferedImage2FXimage(mat2BufferedImage(outImage)));
	}
	
//	public void inputSize() {
//		Stage primaryStage = new Stage();
//		primaryStage.setTitle("Input The Size");
//		primaryStage.setWidth(300);
//		primaryStage.setHeight(200);
//        try {
//        	AnchorPane anchorPane = (AnchorPane)FXMLLoader.load(getClass().getResource("InputSize.fxml")); 
//        	primaryStage.setScene(new Scene(anchorPane));
//        	primaryStage.show();
//        	
//        }catch(Exception ex) {
//        	ex.printStackTrace();
//        }
//        
//	}
	
	public void resizeImage() {
//		Size newSize = new Size(200, 200);
//		Imgproc.resize(originImage, outImage, newSize); //OpenCV Function
		int w0 = originImage.width();
		int h0 = originImage.height();
		int w1 = 200;
		int h1 = 200;
		float fw = (w0 * 1.0f) / w1;
		float fh = (h0 * 1.0f) / h1;
		Mat outImage = new Mat(h1, w1, originImage.type());
		
//		for(int y=0; y<outImage.rows(); y++) {
//			for(int x=0; x<outImage.cols(); x++) {
//				double[] temp1 = outImage.get(y, x);
//				double[] temp0 = originImage.get(Math.round((y * fh)), Math.round((x * fw)));
//				temp1[0] = temp0[0];
//				outImage.put(y, x, temp1);
//			}
//		} // Nearest neighbor interpolation
		
		//Bilinear interpolation
		for(int y=0; y<outImage.rows(); y++) {
			for(int x=0; x<outImage.cols(); x++) {
				float dx = x * fw;
				float dy = y * fh;
				int i = (int)dx;
				int j = (int)dy;
				float u = dx - i;
				float v = dy - j;
				double[] temp0_0 = originImage.get(j, i);
				double[] temp0_1 = originImage.get(j, i + 1);
				double[] temp0_2 = originImage.get(j + 1, i);
				double[] temp0_3 = originImage.get(j + 1, i + 1);
				int result =(int) Math.round((1 - u) * (1 - v) * temp0_0[0] + (1 - u) * v * temp0_1[0] + u * (1 - v) * temp0_2[0] + u * v * temp0_3[0]);
				double[] temp1 = originImage.get(y, x);
				temp1[0] = result;
				outImage.put(y, x, temp1);
			}
		}
		
		myImageView.setFitWidth(outImage.width());
		myImageView.setFitHeight(outImage.height());
		myImageView.setImage(bufferedImage2FXimage(mat2BufferedImage(outImage)));
	}
	
	public void rotateImage() {
//		Point point = new Point(originImage.rows()/2, originImage.cols()/2);
//		Mat r = Imgproc.getRotationMatrix2D(point, 45, 1);
//		Imgproc.warpAffine(originImage, outImage, r, originImage.size()); // OpenCV Function
		double angle = Math.PI / 4;
		int w0 = originImage.width();
		int h0 = originImage.height();
		int h1 = (int)Math.ceil(Math.abs(h0 * Math.cos(angle)) + Math.abs(w0 * Math.sin(angle)));
		int w1 = (int)Math.ceil(Math.abs(w0 * Math.cos(angle)) + Math.abs(h0 * Math.sin(angle)));
		Mat outImage = new Mat(w1, h1, originImage.type());
		for(int y=0; y<outImage.rows(); y++) {
			for(int x=0; x<outImage.cols(); x++) {
				int dx = (int)Math.round(x * Math.cos(angle) + y * Math.sin(angle) - 0.5 * w1 * Math.cos(angle) - 0.5 * h1 * Math.sin(angle) + 0.5 * w0);
				int dy = (int)Math.round(-1 * x * Math.sin(angle) + y * Math.cos(angle) + 0.5 * w1 * Math.sin(angle) - 0.5 * h1 * Math.cos(angle) + 0.5 * h0);
				if(dx < 1 || dx > w0 - 1 || dy < 1 || dy > h0 - 1) {
					dx = 0;
					dy = 0;
				}
				double[] temp0 = originImage.get(dy, dx);
				double[] temp1 = outImage.get(y, x);
				temp1[0] = temp0[0];
				outImage.put(y, x, temp1);
			}
		}
		myImageView.setFitWidth(w1);
		myImageView.setFitHeight(h1);
		myImageView.setImage(bufferedImage2FXimage(mat2BufferedImage(outImage)));
	}
	
	public void geometricTransform() {
		int w0 = originImage.width();
		int h0 = originImage.height();
		int h1 = h0;
		int w1 = w0;
		Mat outImage = new Mat(w1, h1, originImage.type());
		for(int y=0; y<outImage.rows(); y++) {
			for(int x=0; x<outImage.cols(); x++) {
				int dy = (int)(y + 20 * Math.sin((2 * Math.PI * x) / 128));
				int dx = x;
				if(dx < 1 || dx > w0 - 1 || dy < 1 || dy > h0 - 1) {
					dx = 0;
					dy = 0;
				}
				double[] temp0 = originImage.get(dy, dx);
				double[] temp1 = outImage.get(y, x);
				temp1[0] = temp0[0];
				outImage.put(y, x, temp1);
			}
		}
		myImageView.setFitWidth(w1);
		myImageView.setFitHeight(h1);
		myImageView.setImage(bufferedImage2FXimage(mat2BufferedImage(outImage)));
	}
	
	public void geometricTransform2() {
		int w0 = originImage.width();
		int h0 = originImage.height();
		int h1 = h0;
		int w1 = w0;
		Mat outImage = new Mat(w1, h1, originImage.type());
		for(int y=0; y<outImage.rows(); y++) {
			for(int x=0; x<outImage.cols(); x++) {
//				int dy = (int)(y + 20 * Math.sin((2 * Math.PI * x) / 128));
//				int dx = x;
				int dy = (int)(y + 20 * Math.sin((2 * Math.PI * y) / 30));
				int dx = x;
				if(dx < 1 || dx > w0 - 1 || dy < 1 || dy > h0 - 1) {
					dx = 0;
					dy = 0;
				}
				double[] temp0 = originImage.get(dy, dx);
				double[] temp1 = outImage.get(y, x);
				temp1[0] = temp0[0];
				outImage.put(y, x, temp1);
			}
		}
		myImageView.setFitWidth(w1);
		myImageView.setFitHeight(h1);
		myImageView.setImage(bufferedImage2FXimage(mat2BufferedImage(outImage)));
	}
	
	public BufferedImage mat2BufferedImage(Mat m){
		    int type = BufferedImage.TYPE_BYTE_GRAY;
		    if ( m.channels() > 1 ) {
		        type = BufferedImage.TYPE_3BYTE_BGR;
		    }
		    int bufferSize = m.channels()*m.cols()*m.rows();
		    byte [] b = new byte[bufferSize];
		    m.get(0,0,b);
		    BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
		    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		    System.arraycopy(b, 0, targetPixels, 0, b.length);  
		    return image;
	}
	public javafx.scene.image.Image bufferedImage2FXimage(BufferedImage img){
		javafx.scene.image.Image fxImage = SwingFXUtils.toFXImage(img, null);
	
		return fxImage;
	}
	
	
}
