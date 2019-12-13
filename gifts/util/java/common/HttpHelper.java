package com.augmentum.taxtool.utils;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.augmentum.taxtool.constant.Constant;
import com.augmentum.taxtool.exception.OApiException;

public class HttpHelper {

    private static Logger logger = LoggerFactory.getLogger(HttpHelper.class);

    public static JSONObject httpGet(String url) throws OApiException {

        logger.info(url);
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
        httpGet.setConfig(requestConfig);

        logger.info(requestConfig.toString());
        try {
            response = httpClient.execute(httpGet, new BasicHttpContext());
            logger.info("response: " + response.toString());
            if (response.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            logger.info("entity: " + entity.toString());
            if (entity != null) {
                String resultStr = EntityUtils.toString(entity, Constant.UTF8);

                logger.info("resultStr  " + resultStr);
                JSONObject result = JSONObject.fromObject(resultStr);
                logger.info("result  " + result);
                if (result.getInt(Constant.CODE) == 0) {
                    return result;
                } else {
                    int errCode = result.getInt(Constant.CODE);
                    String errMsg = result.getString(Constant.MESSAGE);
                    throw new OApiException(errCode, errMsg);
                }
            }
        } catch (IOException e) {
            logger.error("IOEXCEPTION " + e.getMessage(), e);
            throw new OApiException();
        } finally {
            if (response != null)
                try {
                    response.close();
                } catch (IOException e) {
                    throw new OApiException();
                }
        }

        return null;
    }

    public static JSONObject httpPost(String url, Object data) throws OApiException {

        logger.info(url);
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(36000)
                .setConnectTimeout(3600)
                .build();
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("Content-Type", "application/json");

        try {
            StringEntity requestEntity = new StringEntity(JSONObject.fromObject(data).toString(), Constant.UTF8);
            logger.error("requestEntity " + requestEntity);
            httpPost.setEntity(requestEntity);
            response = httpClient.execute(httpPost, new BasicHttpContext());
            logger.error("response " + response);
            if (response.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String resultStr = EntityUtils.toString(entity, Constant.UTF8);
                logger.error("resultStr " + response);
                JSONObject result = JSONObject.fromObject(resultStr);
                logger.error("result " + result);
                logger.error("CODE  " + result.getInt(Constant.CODE));

                if (result.getInt(Constant.CODE) == 0) {
                    result.remove(Constant.CODE);
                    result.remove(Constant.MESSAGE);
                    return result;
                } else {
                    logger.error("result " + result);
                    int errCode = result.getInt(Constant.CODE);
                    String errMsg = result.getString(Constant.MESSAGE);
                    logger.error("errMsg " + errMsg);
                    throw new OApiException(errCode, errMsg);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            if (response != null)
                try {
                    response.close();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
        }

        return null;
    }
}
