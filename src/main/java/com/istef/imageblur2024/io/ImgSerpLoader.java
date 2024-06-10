package com.istef.imageblur2024.io;

import com.istef.imageblur2024.exceptios.MyException;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest;
import java.time.Duration;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ImgSerpLoader implements ImgLoader {
    private String inputFileType = "";

    public String getInputFileType() {
        return inputFileType;
    }

    @Override
    public @Nullable BufferedImage load(String searchItem) throws MyException {
        HttpClient client = HttpClient.newBuilder().build();

        String uri = String.format("%s?q=%s&engine=google_images&ijn=0&api_key=%s",
                SecretKeys.BASE_URL, searchItem, SecretKeys.API_KEY);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .GET().build();

        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            JSONParser jsonParser = new JSONParser();
            JSONObject responseBody = (JSONObject) jsonParser.parse(response.body());


            String errorRes = (String) responseBody.get("error");
            if (errorRes != null) throw new MyException(errorRes);

            JSONArray imagerResults = (JSONArray) responseBody.get("images_results");
            JSONObject imgRes = (JSONObject) imagerResults.get(0);
            String imgUrl = (String) imgRes.get("original");

            URL url = new URL(imgUrl);
            inputFileType = url.getPath().substring(url.getPath().lastIndexOf(".") + 1);

            return ImageIO.read(new URL(imgUrl));
        } catch (IOException | InterruptedException | ParseException e) {
            throw new MyException(e.getMessage());
        }
    }
}