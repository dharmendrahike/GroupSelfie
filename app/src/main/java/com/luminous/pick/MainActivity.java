package com.luminous.pick;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EmptyStackException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class MainActivity extends Activity {

	GridView gridGallery;
	ImageView FinalImage;
	Handler handler;
	GalleryAdapter adapter;

	ImageView imgSinglePick;
	Button btnGalleryPick;
	Button btnGalleryPickMul;

	String action;
	ViewSwitcher viewSwitcher;
	ImageLoader imageLoader;
	GroupSelfie mGroupSelfie;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		initImageLoader();
		init();
		mGroupSelfie = new GroupSelfie();
	}

	private void initImageLoader() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(defaultOptions).memoryCache(
				new WeakMemoryCache());

		ImageLoaderConfiguration config = builder.build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
	}

	private void init() {

		handler = new Handler();
		/*gridGallery = (GridView) findViewById(R.id.gridGallery);
		gridGallery.setFastScrollEnabled(true);
		adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
		adapter.setMultiplePick(false);*/
		/*gridGallery.setAdapter(adapter);*/

        FinalImage = (ImageView)findViewById(R.id.final_image);
		viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
		viewSwitcher.setDisplayedChild(1);

		imgSinglePick = (ImageView) findViewById(R.id.imgSinglePick);
/*
		btnGalleryPick = (Button) findViewById(R.id.btnGalleryPick);
		btnGalleryPick.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(Action.ACTION_PICK);
				startActivityForResult(i, 100);

			}
		});*/

		btnGalleryPickMul = (Button) findViewById(R.id.btnGalleryPickMul);
		btnGalleryPickMul.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
				startActivityForResult(i, 200);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
			adapter.clear();

			viewSwitcher.setDisplayedChild(1);
			String single_path = data.getStringExtra("single_path");
			imageLoader.displayImage("file://" + single_path, imgSinglePick);

		} else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
			String[] all_path = data.getStringArrayExtra("all_path");

			ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
			ArrayList<byte[]> dataBytes = new ArrayList<byte[]>();
			int imageWidth = 0;
			int imageHeight = 0;
			boolean success = false;

			for (String string : all_path) {
				CustomGallery item = new CustomGallery();
				item.sdcardPath = string;

				/*File file = new File(string);
				FileInputStream fis = null;

                try {
					fis = new FileInputStream(file);
				}
				catch (Exception e){
                    e.printStackTrace();
				}
				//create FileInputStream which obtains input bytes from a file in a file system
				//FileInputStream is meant for reading streams of raw bytes such as image data. For reading streams of characters, consider using FileReader.

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				try {
					for (int readNum; (readNum = fis.read(buf)) != -1;) {
						//Writes to this byte array output stream
						bos.write(buf, 0, readNum);
						System.out.println("read " + readNum + " bytes,");
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}

				byte[] bytes = bos.toByteArray();
				dataByptes.add(bytes);*/
				Bitmap bitmap = null;

				BitmapFactory.Options option = new BitmapFactory.Options();
				option.inPreferredConfig = Bitmap.Config.ARGB_8888;
				bitmap = BitmapFactory.decodeFile(item.sdcardPath);
				imageWidth = bitmap.getWidth();
				imageHeight = bitmap.getHeight();
				FinalImage.setImageBitmap(bitmap);
				byte[] bytes = rgbValuesFromBitmap(bitmap);
				dataBytes.add(bytes);
				dataT.add(item);
			}

			viewSwitcher.setDisplayedChild(0);
			/*adapter.addAll(dataT);*/
			try {
				success = mGroupSelfie.groupSelfie(dataBytes.get(0), dataBytes.get(1), dataBytes.get(2), imageHeight, imageWidth);
			} catch (Exception e){
				e.printStackTrace();
			}
			if(success){
				Log.i("dharmendra","booleean"+ success);
			}
			/*if(success){
				Bitmap finalBitmap;
				byte[] b = mGroupSelfie.getoutPutBufferData();
				BitmapFactory.Options option = new BitmapFactory.Options();
				option.inPreferredConfig = Bitmap.Config.ARGB_8888;
				finalBitmap = BitmapFactory.decodeFile(b.toString());
				FinalImage.setImageBitmap(finalBitmap);
			}*/
		}

	}

	private byte[] rgbValuesFromBitmap(Bitmap bitmap) {
		ColorMatrix colorMatrix = new ColorMatrix();
		ColorFilter colorFilter = new ColorMatrixColorFilter(
				colorMatrix);
		Bitmap argbBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(argbBitmap);

		Paint paint = new Paint();

		paint.setColorFilter(colorFilter);
		canvas.drawBitmap(bitmap, 0, 0, paint);

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int componentsPerPixel = 3;
		int totalPixels = width * height;
		int totalBytes = totalPixels * componentsPerPixel;

		byte[] rgbValues = new byte[totalBytes];
		int[] argbPixels = new int[totalPixels];
		argbBitmap.getPixels(argbPixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < totalPixels; i++) {
			int argbPixel = argbPixels[i];
			int red = Color.red(argbPixel);
			int green = Color.green(argbPixel);
			int blue = Color.blue(argbPixel);
			rgbValues[i * componentsPerPixel + 0] = (byte) red;
			rgbValues[i * componentsPerPixel + 1] = (byte) green;
			rgbValues[i * componentsPerPixel + 2] = (byte) blue;
		}

		return rgbValues;
	}
}
