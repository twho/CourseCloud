package com.coursecloud.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.coursecloud.constants.URLConstants;

public class PHPUtilities implements URLConstants {

	public static final String CONNECT_OK = "connectOK";

	public PHPUtilities() {
	}

	public String checkMacExist(String... params) {
		try {
			String mac = (String) params[0];
			String data = URLEncoder.encode("mac", "UTF-8") + "="
					+ URLEncoder.encode(mac, "UTF-8");
			URL url = new URL(URL_CHECK_MAC);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			// Read Server Response
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				break;
			}
			return sb.toString();
		} catch (Exception e) {
			return new String("Exception: " + e.getMessage());
		}
	}

	public String addMember(String... arg0) {
		try {
			String name = (String) arg0[0];
			String student_id = (String) arg0[1];
			String email = (String) arg0[2];
			String mac = (String) arg0[3];
			String gcm_id = (String) arg0[4];
			String data = URLEncoder.encode("name", "UTF-8") + "="
					+ URLEncoder.encode(name, "UTF-8");
			data += "&" + URLEncoder.encode("student_id", "UTF-8") + "="
					+ URLEncoder.encode(student_id, "UTF-8");
			data += "&" + URLEncoder.encode("email", "UTF-8") + "="
					+ URLEncoder.encode(email, "UTF-8");
			data += "&" + URLEncoder.encode("mac", "UTF-8") + "="
					+ URLEncoder.encode(mac, "UTF-8");
			data += "&" + URLEncoder.encode("gcm_id", "UTF-8") + "="
					+ URLEncoder.encode(gcm_id, "UTF-8");
			URL url = new URL(URL_ADD_MEMBER);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			// Read Server Response
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				Log.i("status", sb + "");
				break;
			}
			return sb.toString();
		} catch (Exception e) {
			return new String("Exception: " + e.getMessage());
		}
	}

	public String getMemberList(String... arg0) {
		try {
			String stringToQuery = (String) arg0[0];
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(URL_GET_MEMBER_LIST);
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("query_string", stringToQuery));
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream inputStream = httpEntity.getContent();

			BufferedReader bufReader = new BufferedReader(
					new InputStreamReader(inputStream, "utf-8"), 8);
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = bufReader.readLine()) != null) {
				builder.append(line + "\n");
			}
			inputStream.close();
			return builder.toString();
		} catch (Exception e) {
			return new String("Exception: " + e.getMessage());
		}
	}

	// TODO not finish
	public String getScoreList(String... arg0) {
		try {
			String stringToQuery = (String) arg0[0];
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(URL_GET_SCORE_LIST);
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("query_string", stringToQuery));
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream inputStream = httpEntity.getContent();

			BufferedReader bufReader = new BufferedReader(
					new InputStreamReader(inputStream, "utf-8"), 8);
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = bufReader.readLine()) != null) {
				builder.append(line + "\n");
			}
			inputStream.close();
			return builder.toString();
		} catch (Exception e) {
			return new String("Exception: " + e.getMessage());
		}
	}

	public String addTeacher(String... arg0) {
		String teacher = (String) arg0[0];
		String mac = (String) arg0[1];
		String gcm_id = (String) arg0[2];
		// String data = "name=" + name + "&mac=" + mac + "&gcm_id=" + gcm_id;
		// String dataEncode = URLEncoder.encode(data, "UTF-8");

		String data = "";
		try {
			data = URLEncoder.encode("teacher", "UTF-8") + "="
					+ URLEncoder.encode(teacher, "UTF-8");
			data += "&" + URLEncoder.encode("mac", "UTF-8") + "="
					+ URLEncoder.encode(mac, "UTF-8");
			data += "&" + URLEncoder.encode("gcm_id", "UTF-8") + "="
					+ URLEncoder.encode(gcm_id, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return connectURL(data);
	}

	private String connectURL(String data) {
		try {
			URL url = new URL(URL_ADD_TEACHER);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			// Read Server Response
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				Log.i("status", sb + "");
				break;
			}
			return sb.toString();
		} catch (Exception e) {
			return new String("Exception: " + e.getMessage());
		}
	}

	public String addRateData(String... arg0) {
		try {
			String name = (String) arg0[0];
			String mac = (String) arg0[1];
			String team = (String) arg0[2];
			String rate = (String) arg0[3];
			Log.i("team", rate);
			String data = URLEncoder.encode("name", "UTF-8") + "="
					+ URLEncoder.encode(name, "UTF-8");
			data += "&" + URLEncoder.encode("mac", "UTF-8") + "="
					+ URLEncoder.encode(mac, "UTF-8");
			data += "&" + URLEncoder.encode("team", "UTF-8") + "="
					+ URLEncoder.encode(team, "UTF-8");
			data += "&" + URLEncoder.encode("rate", "UTF-8") + "="
					+ URLEncoder.encode(rate, "UTF-8");
			URL url = new URL(URL_POST_SCORE);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			// Read Server Response
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				Log.i("status", sb + "");
				break;
			}
			return sb.toString();
		} catch (Exception e) {
			return new String("Exception: " + e.getMessage());
		}
	}

	public String getRateResult(String... arg0) {
		try {
			String team = (String) arg0[0];
			String link = URL_GET_SCORE_RESULT + "?team=" + team;
			URL url = new URL(link);
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(link));
			HttpResponse response = client.execute(request);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line);
				break;
			}
			in.close();
			return sb.toString();
		} catch (Exception e) {
			return new String("Exception: " + e.getMessage());
		}
	}

	public String getRateCount(String... arg0) {
		try {
			String mac = (String) arg0[0];
			String link = URL_GET_SCORE_COUNT + "?mac=" + mac;
			URL url = new URL(link);
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(link));
			HttpResponse response = client.execute(request);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line);
				break;
			}
			in.close();
			return sb.toString();
		} catch (Exception e) {
			return new String("Exception: " + e.getMessage());
		}
	}

	public String getRateRecord(String... arg0) {
		try {
			String mac = (String) arg0[0];
			String link = URL_GET_SCORE_RECORD + "?mac=" + mac;
			URL url = new URL(link);
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(link));
			HttpResponse response = client.execute(request);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line);
				break;
			}
			in.close();
			return sb.toString();
		} catch (Exception e) {
			return new String("Exception: " + e.getMessage());
		}
	}

	public String postPortrait(String... arg0) {
		try {
			String mac = (String) arg0[0];
			String image = (String) arg0[1];
			String data = URLEncoder.encode("mac", "UTF-8") + "="
					+ URLEncoder.encode(mac, "UTF-8");
			data += "&"
					+ URLEncoder.encode("image", "UTF-8")
					+ "="
					+ URLEncoder.encode(
							new ImageUtilities().base64EncodeToString(image),
							"UTF-8");
			URL url = new URL(URL_POST_PORTRAIT);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			// Read Server Response
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				Log.i("status", sb + "");
				break;
			}
			return sb.toString();
		} catch (Exception e) {
			return new String("Exception: " + e.getMessage());
		}
	}

	public String getMyPortrait(String... arg0) {
		try {
			String mac = (String) arg0[0];
			String data = URLEncoder.encode("mac", "UTF-8") + "="
					+ URLEncoder.encode(mac, "UTF-8");
			URL url = new URL(URL_GET_MY_PORTRAIT);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			// Read Server Response
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				Log.i("status", sb + "");
				break;
			}
			return sb.toString();
		} catch (Exception e) {
			return new String("Exception: " + e.getMessage());
		}
	}

	public String checkIn(String... arg0) {
		try {
			String name = (String) arg0[0];
			String mac = (String) arg0[1];
			String time = (String) arg0[2];
			String data = URLEncoder.encode("name", "UTF-8") + "="
					+ URLEncoder.encode(name, "UTF-8");
			data += "&" + URLEncoder.encode("mac", "UTF-8") + "="
					+ URLEncoder.encode(mac, "UTF-8");
			data += "&" + URLEncoder.encode("time", "UTF-8") + "="
					+ URLEncoder.encode(time, "UTF-8");
			URL url = new URL(URL_CHECK_IN);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			// Read Server Response
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				Log.i("status", sb + "");
				break;
			}
			return sb.toString();
		} catch (Exception e) {
			return new String("Exception: " + e.getMessage());
		}
	}

	public String checkIP(String... arg0) {
		try {
			String ip = (String) arg0[0];
			String data = URLEncoder.encode("ip", "UTF-8") + "="
					+ URLEncoder.encode(ip, "UTF-8");
			URL url = new URL(URL_CHECK_IP);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			// Read Server Response
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				Log.i("status", sb + "");
				break;
			}
			return sb.toString();
		} catch (Exception e) {
			return new String("Exception: " + e.getMessage());
		}
	}

}
