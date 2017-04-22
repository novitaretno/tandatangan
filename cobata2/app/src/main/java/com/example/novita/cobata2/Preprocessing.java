package com.example.novita.cobata2;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.NULL;
import static org.opencv.imgproc.Imgproc.CV_MEDIAN;
import static org.opencv.imgproc.Imgproc.contourArea;
import static org.opencv.imgproc.Imgproc.equalizeHist;
import static org.opencv.imgproc.Imgproc.isContourConvex;

/**
 * Created by Administrator on 3/6/2017.
 */

public class Preprocessing extends AppCompatActivity {

    private static final String TAG = "Preprocessing";
    private int x1, x2, x3, x4;
    private int xa,ya, xb, yb, xc, yc, xd, yd, i, j;
    private int baris, kolom;
    private int tinggi_img_baru, lebar_img_baru;
    private int lebar_new, tinggi_new;
    private double lebar_new2, tinggi_new2;
    private ImageView image_source, image_signature, image_source_crop,image_source_bound,
            image_source_noise, image_signature_noise;
    private Bitmap bitmap1, bitmap2, new_img1, img_bound;
    private int vertical_max, horisontal_max;


    private void preprocessing() {

        bitmap1.getHeight();
        bitmap1.getWidth();
        bitmap2.getHeight();
        bitmap2.getWidth();


        Log.v("log_tag", "size 1: " + "height" + bitmap1.getHeight() + "width" + bitmap1.getWidth());
        Log.v("log_tag", "size 1: " + "height" + bitmap2.getHeight() + "width" + bitmap2.getWidth());

        int tinggi_img1 =bitmap1.getHeight();
        int lebar_img1= bitmap1.getWidth();

        int R=0;
        Bitmap gray_1 = Bitmap.createBitmap( bitmap1.getWidth(), bitmap1.getHeight(), bitmap1.getConfig());

        for(ya=0; ya<tinggi_img1; ya++) {
            for (xa = 0; xa < lebar_img1; xa++) {
                int pixel = bitmap1.getPixel(xa, ya);
                R = Color.red(pixel);
                if (R > 128)
                    R = 255;
                else
                    R = 0;
                gray_1.setPixel(xa, ya, Color.rgb(R, R, R));
                if (R == 0) {
                    x1 = ya;
                    Log.v("log_tag", "x1 ->" + x1);
                    break;}
            }

            if (R == 0) {break;}
        }


        for( xb=0; xb<lebar_img1; xb++){
            for(yb=0; yb<tinggi_img1; yb++) {
                int pixel = bitmap1.getPixel(xb, yb);
                R = Color.red(pixel);

                if (R > 128)
                    R = 255;
                else
                    R = 0;

                gray_1.setPixel(xb, yb, Color.rgb(R, R, R));

                if (R == 0) {
                    x2 = xb;
                    Log.v("log_tag", "x2 ->" + x2); break; }
            }
            if (R == 0){break;}
        }

        for(xc=lebar_img1-1; xc >=0; xc--){
            for(yc=0; yc < tinggi_img1; yc++){
                int pixel = bitmap1.getPixel(xc, yc);
                R = Color.red(pixel);

                if (R > 128)
                    R = 255;
                else
                    R = 0;

                gray_1.setPixel(xc, yc, Color.rgb(R, R, R));

                if (R == 0) {
                    x3 = xc;
                    Log.v("log_tag", "x3 ->" + x3); break;}
            }
            if (R == 0){break;}
        }

        for(yd=tinggi_img1-1; yd>=0; yd--){
            for(xd=0; xd<lebar_img1; xd++){
                int pixel = bitmap1.getPixel(xd, yd);
                R = Color.red(pixel);

                if (R > 128)
                    R = 255;
                else
                    R = 0;

                gray_1.setPixel(xd, yd, Color.rgb(R, R, R));

                if (R == 0) {
                    x4 = yd;
                    Log.v("log_tag", "x4 ->" + x4); break;}
            }
            if (R == 0){break;}
        }
        lebar_new = x3-x2;
        Log.v("log_tag", "lebar_new ->" + lebar_new);
        tinggi_new = x4-x1;
        Log.v("log_tag", "tinggi_new ->" + tinggi_new);


        new_img1 = Bitmap.createBitmap(lebar_new,tinggi_new,Bitmap.Config.RGB_565);

        for(i=0; i<lebar_new; i++){
            //Log.v("log_tag", "lala");
            for(j=0; j<tinggi_new; j++){
                int pixel = bitmap1.getPixel(x2+i, x1+j);
                new_img1.setPixel(i,j,pixel);
            }

        }
        Log.v("log_tag", "ukuran image baru ->" + new_img1);
        Log.v("log_tag", "image baru ->" + new_img1.getHeight() + " " + new_img1.getWidth());

    }

    private void ekstraksi_fitur()
    {
        //grayscale image crop
        tinggi_img_baru = new_img1.getHeight();
        lebar_img_baru =new_img1.getWidth();
        int kolom_max=0, kolom_temp, R=0;
        Bitmap gray_img_crop = Bitmap.createBitmap( bitmap1.getWidth(), bitmap1.getHeight(), bitmap1.getConfig());

        for(ya=0; ya<tinggi_img_baru; ya++) {
            for (xa = 0; xa < lebar_img_baru; xa++) {
                int pixelnya = new_img1.getPixel(xa, ya);
                // Log.v("log_tag", "pixel for 1 ->" + pixel);
                R = Color.red(pixelnya);
                if (R > 128)
                    R = 255;
                else
                    R = 0;
                gray_img_crop.setPixel(xa, ya, Color.rgb(R, R, R));
            }
        }
        //max horisontal;
        for(baris=0; baris<tinggi_img_baru; baris++){
            kolom_temp=0;
            for(kolom=0; kolom<lebar_img_baru; kolom++){
                int pixel = gray_img_crop.getPixel(kolom, baris);
                // Log.v("log_tag", "warna img1 kolom ke -> " + kolom + "warna -> "+gray_img_crop.getPixel(kolom, baris));
                if(pixel == Color.BLACK){
                    kolom_temp=kolom_temp+1;
                    //   Log.v("log_tag", "kolom temp-> " + kolom_temp);
                }
            }
            if(kolom_temp > kolom_max) {
                kolom_max = kolom_temp;
                horisontal_max = baris;

            }

        }
        Log.v("log_tag", "horisontal max, baris ke  -> " + horisontal_max+ "jumlah max hitam -> " + kolom_max);

        //max vertical;
        for(kolom=0; kolom<lebar_img_baru; kolom++){
            kolom_temp=0;
            for(baris=0; baris<tinggi_img_baru; baris++){
                int pixel = gray_img_crop.getPixel(kolom, baris);
                // Log.v("log_tag", "warna img1 kolom ke -> " + kolom + "warna -> "+gray_img_crop.getPixel(kolom, baris));
                if(pixel == Color.BLACK){
                    kolom_temp=kolom_temp+1;
                    //   Log.v("log_tag", "kolom temp-> " + kolom_temp);
                }
            }
            if(kolom_temp > kolom_max) {
                kolom_max = kolom_temp;
                vertical_max = kolom;

            }

        }
        Log.v("log_tag", "vertical max, kolom ke -> " + vertical_max+ "jumlah max hitam -> " + kolom_max);

        MatOfPoint2f approxCurve = new MatOfPoint2f();
        Mat imgMAT = new Mat();
        Utils.bitmapToMat(gray_img_crop, imgMAT);
        Mat grad = new Mat();
        Imgproc.cvtColor(imgMAT, grad, Imgproc.COLOR_BGR2GRAY);
        Mat img_sobel = new Mat();
        Mat img_threshold = new Mat();

        //Bitmap image;
        Imgproc.Sobel(grad, img_sobel, CvType.CV_8U, 1, 0, 3, 1, 0, Core.BORDER_DEFAULT);

        Imgproc.threshold(img_sobel, img_threshold, 0, 255, Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY);

        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(7, 13));

        Imgproc.morphologyEx(img_threshold, img_threshold, Imgproc.MORPH_CLOSE, element);

        Imgproc.cvtColor(imgMAT, imgMAT, Imgproc.COLOR_BGR2GRAY);


        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(img_threshold, contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE, new org.opencv.core.Point(0, 0));

        //list moment/center of mask
        List<MatOfPoint2f> mc = new ArrayList<MatOfPoint2f>(contours.size());

        List<Moments> mu = new ArrayList<Moments>(contours.size());


        Log.v("log_tag", "contour size -> " + contours.size());

        for (int i = 0; i < contours.size(); i++) {

            //Convert contours(i) from MatOfPoint to MatOfPoint2f
            MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );

            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

            // Get bounding rect of contour
            org.opencv.core.Rect rect = Imgproc.boundingRect(points);
            Log.v("log_tag", "rect -> " + rect);

            // get area of signature
            rect.area();
            Log.v("log_tag", "area -> " + rect.area());

            // get line bounding box
            Imgproc.rectangle(imgMAT, rect.tl(), rect.br(), new Scalar(128, 128, 128), 2);

            //center of mass
            mu.add(i, Imgproc.moments(contours.get(i), false));
            Moments p = mu.get(i);
            int x = (int) (p.get_m10() / p.get_m00());
            int y = (int) (p.get_m01() / p.get_m00());

            // Mat rgbaImage = new Mat();
            Imgproc.circle(imgMAT, new Point(x, y), 10, new Scalar(255,215,0,255));
            Log.v("log_tag", "x -> " + x + ", i -> " + i);
            Log.v("log_tag", "y -> " + y + ",  i -> " + i);
            String file = Environment.getExternalStorageDirectory().getPath() + "/DigitSign/"+"/Center_of_Mass.jpg";
            Imgcodecs.imwrite(file,imgMAT);

        }

        //aspect ratio
        double Ratio;
        lebar_new2 =lebar_new;
        tinggi_new2 = tinggi_new;
        Ratio = lebar_new2/tinggi_new2;
        Log.v("log_tag", "ratio -> " + Ratio);


        //code hamming gambar di dd
        int panjang_gambar = bitmap1.getHeight();
        int lebar_gambar = bitmap1.getWidth();
        int beda_pixel=0;
        for (int i=0; i<lebar_gambar; i++){
            for(int j=0; j<panjang_gambar; j++)
            {
                int pixel_gbr_1 = bitmap1.getPixel(i,j);
                int pixel_gbr_2 = bitmap2.getPixel(i,j);
                // Log.v("log_tag", "warna img1 kolom ke -> " + kolom + "warna -> "+gray_img_crop.getPixel(kolom, baris));
                if(pixel_gbr_1 != pixel_gbr_2){
                    beda_pixel=beda_pixel+1;
                    //   Log.v("log_tag", "kolom temp-> " + kolom_temp);
                }
            }
        }
        Log.v("log_tag", "beda pixel -> " + beda_pixel);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_signature);

        String ImageSourcePath = Environment.getExternalStorageDirectory().getPath() + "/DigitSign/" + "source.png";
        String ImageSignaturePath = Environment.getExternalStorageDirectory().getPath()+ "/DigitSign/" + "signature.png";

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize=8;

        bitmap1 = BitmapFactory.decodeFile(ImageSourcePath);
        bitmap2 = BitmapFactory.decodeFile(ImageSignaturePath);

        image_source = (ImageView) findViewById(R.id.img_source);
        image_source.setImageBitmap(bitmap1);

        image_signature = (ImageView) findViewById(R.id.img_ttd);
        image_signature.setImageBitmap(bitmap2);

        //for computing filter
        preprocessing();
        ekstraksi_fitur();

        image_source_crop = (ImageView) findViewById(R.id.img_source_crop);
        image_source_crop.setImageBitmap(new_img1);

    }
}
