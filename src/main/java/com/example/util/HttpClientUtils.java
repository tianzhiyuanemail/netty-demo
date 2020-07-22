package com.example.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HttpClient4.3工具类
 *
 * @author hang.luo
 */
public class HttpClientUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class); // 日志记录
    private static final int CONN_REQ_TIMEOUT = 10000;
    private static final int SOCKET_TIMEOUT = 10000;
    private static final int CONN_TIMEOUT = 10000;
    private static RequestConfig requestConfig = null;

    static {
        // 设置请求和传输超时时间
        requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
    }

    /**
     * post请求传输json参数
     *
     * @param url url地址
     * @param
     * @return
     */
    public static JSONObject httpPost(String url, JSONObject jsonParam) {
        // post请求返回结果
        CloseableHttpClient httpClient = HttpClients.createDefault();
        JSONObject jsonResult = null;
        HttpPost httpPost = new HttpPost(url);
        // 设置请求和传输超时时间
        httpPost.setConfig(requestConfig);
        try {
            if (null != jsonParam) {
                // 解决中文乱码问题
                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }
            CloseableHttpResponse result = httpClient.execute(httpPost);
            // 请求发送成功，并得到响应
            if (result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String str = "" ;
                try {
                    // 读取服务器返回过来的json字符串数据
                    str = EntityUtils.toString(result.getEntity(), "utf-8");
                    // 把json字符串转换成json对象
                    jsonResult = JSONObject.parseObject(str);
                } catch (Exception e) {
                    logger.error("post请求提交失败:" + url, e);
                }
            }
        } catch (IOException e) {
            logger.error("post请求提交失败:" + url, e);
        } finally {
            httpPost.releaseConnection();
        }
        return jsonResult;
    }

    /**
     * post请求传输String参数 例如：name=Jack&sex=1&type=2
     * Content-type:application/x-www-form-urlencoded
     *
     * @param url      url地址
     * @param strParam 参数
     * @return
     */
    public static JSONObject httpPost(String url, String strParam) {
        // post请求返回结果
        CloseableHttpClient httpClient = HttpClients.createDefault();
        JSONObject jsonResult = null;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        try {
            if (null != strParam) {
                // 解决中文乱码问题
                StringEntity entity = new StringEntity(strParam, "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }
            CloseableHttpResponse result = httpClient.execute(httpPost);
            // 请求发送成功，并得到响应
            if (result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String str = "" ;
                try {
                    // 读取服务器返回过来的json字符串数据
                    str = EntityUtils.toString(result.getEntity(), "utf-8");
                    // 把json字符串转换成json对象
                    jsonResult = JSONObject.parseObject(str);
                } catch (Exception e) {
                    logger.error("post请求提交失败:" + url, e);
                }
            }
        } catch (IOException e) {
            logger.error("post请求提交失败:" + url, e);
        } finally {
            httpPost.releaseConnection();
        }
        return jsonResult;
    }

    /**
     * 发送get请求
     *
     * @param url 路径
     * @return
     */
    public static JSONObject httpGet(String url) {
        // get请求返回结果
        JSONObject jsonResult = null;
        CloseableHttpClient client = HttpClients.createDefault();
        // 发送get请求
        HttpGet request = new HttpGet(url);
        request.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = client.execute(request);

            // 请求发送成功，并得到响应
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 读取服务器返回过来的json字符串数据
                HttpEntity entity = response.getEntity();
                String strResult = EntityUtils.toString(entity, "utf-8");
                // 把json字符串转换成json对象
                jsonResult = JSONObject.parseObject(strResult);
            } else {
                logger.error("get请求提交失败:" + url);
            }
        } catch (IOException e) {
            logger.error("get请求提交失败:" + url, e);
        } finally {
            request.releaseConnection();
        }
        return jsonResult;
    }


    /**
     * 发送Post:json请求
     * url
     * json
     * ClientProtocolException
     * IOException
     */
    public static String sendPostJson(String url, String json) throws ClientProtocolException,
            IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();//wrapClient();
        HttpPost post = new HttpPost(url);
        //String encoderJson = URLEncoder.encode(json, HTTP.UTF_8);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONN_REQ_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectTimeout(CONN_TIMEOUT).build();// 设置请求和传输超时时间
        post.setConfig(requestConfig);
        StringEntity myEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        post.setEntity(myEntity);
        CloseableHttpResponse resp = client.execute(post);
        String result = "" ;
        if (resp.getStatusLine().getStatusCode() == 200) {
            result = convertStreamToString(resp);// 获取响应报文
        } else {
            logger.error("响应异常" + resp.getStatusLine() + ","
                    + resp.getStatusLine().getReasonPhrase());
            result = resp.getStatusLine().getStatusCode() + "" ;
        }

        return result;
    }


    /**
     * 发送Post:表单请求
     * url
     * param map表单数据
     * ClientProtocolException
     * IOException
     */
    public static String sendPostForm(String url, Map<String, String> param)
            throws ClientProtocolException,
            IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONN_REQ_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectTimeout(CONN_TIMEOUT).build();// 设置请求和传输超时时间
        post.setConfig(requestConfig);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        CloseableHttpResponse resp = client.execute(post);
        String result = "" ;
        if (resp.getStatusLine().getStatusCode() == 200) {
            result = convertStreamToString(resp);// 获取响应报文
        } else {
            logger.error("响应异常" + resp.getStatusLine() + ","
                    + resp.getStatusLine().getReasonPhrase());
            result = resp.getStatusLine().getStatusCode() + "" ;
        }

        return result;
    }

    /**
     * 微信带证书
     * url
     * xml
     * Exception
     */
    @SuppressWarnings("deprecation")
    public static String sendRequestXML(String url, String xml, String encoding) throws Exception {
        String strResult = "" ;
        HttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url); // 创建HttpPost
            StringEntity se = new StringEntity(xml, encoding);
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "text/xml"));
            se.setContentEncoding(encoding);

            httpPost.setEntity(se);
            httpPost.setHeader();
            HttpResponse response = httpClient.execute(httpPost); // 执行POST请求
            // 若状态码为200 ok
            if (response.getStatusLine().getStatusCode() == 200) {
                // 取出回应字串
                strResult = EntityUtils.toString(response.getEntity());
            }
            strResult = new String(strResult.getBytes("ISO-8859-1"), encoding);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpClient != null)
                httpClient.getConnectionManager().shutdown();// 关闭连接,释放资源
        }
        return strResult;

    }

    private static HttpClient getClientWithCert(String certFile, String certPwd) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        FileInputStream instream = new FileInputStream(new File(certFile));
        try {
            keyStore.load(instream, certPwd.toCharArray());
        } finally {
            instream.close();
        }

        // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, certPwd.toCharArray()).build();
        // Allow TLSv1 protocol only
        @SuppressWarnings("deprecation")
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
                new String[]{"TLSv1"}, null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        return httpclient;
    }

    private static String convertStreamToString(CloseableHttpResponse resp)
            throws UnsupportedEncodingException,
            IOException {
        InputStreamReader ir = null;
        StringBuffer sb = new StringBuffer("");
        try {
            HttpEntity entity = resp.getEntity();
            ir = new InputStreamReader(entity.getContent());
            BufferedReader br = new BufferedReader(ir);
            String t = null;
            while ((t = br.readLine()) != null) {
                sb.append(t);
            }
        } finally {
            resp.close();
            if (ir != null) {
                ir.close();
            }
        }
        logger.debug("base64响应报文[{}]" + sb);
        return sb.toString();
    }

    /**
     * 向指定URL发送GET方法的请求
     * url
     * 发送请求的URL
     * param
     * 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "" ;
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 发起https请求并获取结果
     *
     * @param requestUrl    请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr     提交的数据
     * @return JSONObject(通过JSONObject.get ( key)的方式获取json对象的属性值)
     */
    public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {

        JSONObject jsonObject = null;
        StringBuffer buffer = new StringBuffer();
        try {
            // 创建SSLContext对象
            //TrustManager[] tm = { };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, null, new java.security.SecureRandom());

            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
            httpUrlConn.setSSLSocketFactory(ssf);
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);

            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(requestMethod);
            if ("GET".equalsIgnoreCase(requestMethod)) {
                httpUrlConn.connect();
            }

            // 当有数据需要提交时
            if (outputStr != null) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // 将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();

            // 释放资源
            inputStream.close();
            inputStream = null;
            //httpUrlConn.disconnect();
            jsonObject = JSONObject.parseObject(buffer.toString());

        } catch (Exception ce) {
            logger.error("https的请求发送失败:", ce);
        }

        return jsonObject;

    }


    public static void main(String[] args) throws Exception {
        String s = HttpClientUtils.sendRequestXML("http://localhost:8099/xx", "<a>wwwwwwwww</a>", "utf-8");

    }

}