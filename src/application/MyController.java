package application;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.sun.prism.paint.Color;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class MyController {
	@FXML
	private Button originButton;
	@FXML
	private ImageView myImageView;
	@FXML
	private Button negativeButton;
	@FXML
	private Button histogramButton;
	@FXML
	private Button eqButton;
	@FXML
	private Button eqhistogramButton;
	
	Mat originImage = Imgcodecs.imread("F:/lena1.png",Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
	int size = (int)(originImage.total()*originImage.channels());
	Mat negativeImage,eqImage;
	int[] intensity = new int[256];
	public void showOriginalImage(ActionEvent event){		
		myImageView.setImage(bufferedImage2FXimage(mat2BufferedImage(originImage)));
		System.out.println(originImage);
		//Imgcodecs.imwrite("F:/lena2.png",image);
	}
	
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
