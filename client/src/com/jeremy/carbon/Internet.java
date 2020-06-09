package com.jeremy.carbon;

import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

public class Internet {

	public static String publishImage(String carbonUrl, RenderedImage image) {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(carbonUrl + "/create");
			connection = (HttpURLConnection) url.openConnection();

			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Extension", "png");

			DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
			ImageIO.write(image, "png", outputStream);

			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) builder.append(line);
			reader.close();
			return builder.toString();
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		} finally {
			if (connection != null) connection.disconnect();
		}
	}

}
