package ru.android.hellslade.autochmo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap.CompressFormat;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

public class AutochmoApplication extends Application {

	private final String TAG = "AutochmoApplication";
	public ImageLoader imageLoader;

	private String mHost;
	private SharedPreferences mSettings;
	private String mLogin;
	private String mPassword;
	private String mPasswordhash;
	private boolean isLogin = false;
	private String mLastErrorCode;
	private String mLastErrorText;

	@Override
	public void onCreate() {
		// Get singletone instance of ImageLoader
		imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_launcher_autochmo)
				.showImageForEmptyUri(R.drawable.ic_launcher_autochmo)
				.cacheInMemory().cacheOnDisc()
				.imageScaleType(ImageScaleType.POWER_OF_2).build();
		File cacheDir = StorageUtils.getOwnCacheDirectory(
				getApplicationContext(), "Autochmo/Cache");
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.memoryCacheExtraOptions(480, 800)
				.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75)
				// Can slow ImageLoader, use it carefully (Better don't use it)
				.threadPoolSize(5).threadPriority(Thread.MIN_PRIORITY + 2)
				.denyCacheImageMultipleSizesInMemory().offOutOfMemoryHandling()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation
				.discCache(new UnlimitedDiscCache(cacheDir)) // You can pass your own disc cache implementation
				.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
				// .imageDownloader(new DefaultImageDownloader(5 * 1000, 30 *
				// 1000)) // connectTimeout (5 s), readTimeout (30 s)
				.defaultDisplayImageOptions(options)
				// .enableLogging()
				.build();
		// Initialize ImageLoader with created configuration. Do it once.
		imageLoader.init(config);

		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		mHost = this.getString(R.string.host);
		super.onCreate();
	}

	public List<Fact> _getFactList(int offset, int count) {
		return _getFactList(offset, count, "");
	}
	public List<Fact> _getFactList(int offset, int count, String gosnomer) {
		// Add your data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("limit", String.valueOf(count)));
		nameValuePairs.add(new BasicNameValuePair("offset", String.valueOf(offset)));
		nameValuePairs.add(new BasicNameValuePair("gosnomer", gosnomer));
		String responseString = SendDataGet(mHost, nameValuePairs);
		if (responseString == null)
			return null;
		// Распарсить xml-ответ и добавить в imageAndText
		Log.v(responseString);
		FactParser parser = new FactParser(responseString);
		List<Fact> facts = parser.parse();
		Log.v("Count of facts " + facts.size());
		return facts;
	}

	public List<Comment> _getComment(String fact_id) {
		List<Comment> comments;
		String responseString = SendDataGet(
				String.format("%s%s/comments", mHost, fact_id), null);
		// Распарсить xml-ответ и добавить в imageAndText
		Log.v(responseString);
		CommentParser parser = new CommentParser(responseString);
		comments = parser.parse();
		Collections.reverse(comments);
		return comments;
	}

	/**
	 * Получить список марок-моделей машин Метод требует авторизации
	 */
	public List<Carmodel> _getCarModels() {
		String responseString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><autochmoreply></autochmoreply>";
		if (!isOnline()) {
			// Отсутствует подключение к инету, надо подтянуть список машин из
			// кеша
			String buf = readCarmodels();
			responseString = !buf.equalsIgnoreCase("") ? buf : responseString;
		} else {
			_checkAuth();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("login", mLogin));
			nameValuePairs.add(new BasicNameValuePair("password", mPassword));
			responseString = SendDataPost(mHost + "/getcarmodels",
					nameValuePairs);
			saveCarmodels(responseString);
			// Log.v(responseString);
		}
		Log.v("parser create");
		// Очистить responseString от "нежити"
		CarmodelParser parser = new CarmodelParser(responseString);
		Log.v("parser start parse");
		List<Carmodel> carmodels = parser.parse();
		Log.v("parser end parse");
		Log.v("Count of carmodels " + carmodels.size());
		return carmodels;
	}

	public String readCarmodels() {
		File fName = new File(this.getExternalCacheDir(), "carmodels.xml");
		if (fName.isFile() && fName.canRead()) {
			try {
				FileReader reader = new FileReader(fName);
				char[] buf = new char[(int) fName.length()];
				reader.read(buf, 0, (int) fName.length());
				reader.close();
				Log.v("responseString readed from local cache ");
				return String.valueOf(buf).trim();
			} catch (FileNotFoundException e) {
				Log.v("carmodels.xml FileNotFound");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}

	public void saveCarmodels(String response) {
		File fName = new File(this.getExternalCacheDir(), "carmodels.xml");
		if (!fName.exists()) {
			fName.getParentFile().mkdirs();
		}
		try {
			fName.createNewFile();
			FileWriter writer = new FileWriter(fName);
			writer.write(response);
			writer.close();
		} catch (FileNotFoundException e) {
			Log.v("carmodels.xml FileNotFound");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Метод требует авторизации
	 * 
	 * Неиспользуется, достаточно метода _getCarModels
	 */
	public void _getCarMarks() {
		/*
		 * _checkAuth(); List<NameValuePair> nameValuePairs = new
		 * ArrayList<NameValuePair>(2); nameValuePairs.add(new
		 * BasicNameValuePair("login", mLogin)); nameValuePairs.add(new
		 * BasicNameValuePair("password", mPassword)); String responseString =
		 * SendDataPost(mHost + "/getcarmarks", nameValuePairs);
		 * Log.v(responseString);
		 */

	}

	/**
	 * Метод требует авторизацию
	 * 
	 * @param factId
	 * @param vote
	 */
	public String setRate(String factId, String vote) {
		_checkAuth();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("login", mLogin));
		nameValuePairs
				.add(new BasicNameValuePair("passwordhash", mPasswordhash));
		nameValuePairs.add(new BasicNameValuePair("fact_id", factId));
		nameValuePairs.add(new BasicNameValuePair("vote", vote));

		String responseString = SendDataPost(mHost + "/vote", nameValuePairs);
		Log.v(responseString);
		String result = _parseXML(responseString, "callresult");
		if (result.equalsIgnoreCase("ok")) {
			return this.getString(R.string.vote_ok);
			// Toast.makeText(this, this.getString(R.string.vote_ok),
			// Toast.LENGTH_SHORT).show();
		} else if (result.equalsIgnoreCase("fail")) {
			return this.getString(R.string.vote_fail);
			// Toast.makeText(this, this.getString(R.string.vote_fail),
			// Toast.LENGTH_SHORT).show();
		} else {
			_parseXML(responseString, "error", "code");
			return mLastErrorText;
			// Toast.makeText(this, mLastErrorText,
			// Toast.LENGTH_SHORT).show();
		}
	}

	public String _addComment(String factId, String comment) {
		_checkAuth();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("login", mLogin));
		nameValuePairs
				.add(new BasicNameValuePair("passwordhash", mPasswordhash));
		nameValuePairs.add(new BasicNameValuePair("fact_id", factId));
		nameValuePairs.add(new BasicNameValuePair("text", comment));

		String responseString = SendDataPost(mHost + "/addcomment",
				nameValuePairs);
		Log.v(responseString);
		String result = _parseXML(responseString, "callresult");
		if (result.equalsIgnoreCase("ok")) {
			return this.getString(R.string.add_comment_ok);
			// Toast.makeText(this,
			// this.getString(R.string.add_comment_ok),
			// Toast.LENGTH_SHORT).show();
		} else if (result.equalsIgnoreCase("fail")) {
			return this.getString(R.string.add_comment_fail);
			// Toast.makeText(this,
			// this.getString(R.string.add_comment_fail),
			// Toast.LENGTH_SHORT).show();
		} else {
			_parseXML(responseString, "error", "code");
			return mLastErrorText;
			// Toast.makeText(this, mLastErrorText,
			// Toast.LENGTH_SHORT).show();
		}
	}

	public String _addFact(String carmodel_id, String carmodel, String desc,
			String gosnomer, String nonomer, Map<String, String> files) {
		_checkAuth();
		Map<String, String> nameValuePairs = new HashMap<String, String>();
		Log.v("carmodel_id " + carmodel_id);
		Log.v("carmodel " + carmodel);
		Log.v("desc " + desc);
		Log.v("gosnomer " + gosnomer);
		Log.v("nogosnomer " + nonomer);
		Location location = null;
		String latitude = "", longitude = "";
		ExifInterface exif;
		try {
			String fn = files.values().toArray()[0].toString();
			Log.v("fn " + fn);
			exif = new ExifInterface(files.values().toArray()[0].toString());
			geoDegree gps = new geoDegree(exif);
			longitude = gps.toString().split(",", 2)[1];
			latitude = gps.toString().split(",", 2)[0];
			Log.v("Coordinates " + latitude + " " + longitude);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.v("Can not get GPS attributes");
		} finally {
			location = get_location();
		}
		if (location != null) {// && (latitude.isEmpty() ||
								// longitude.isEmpty())) {
			latitude = String.valueOf(location.getLatitude());
			longitude = String.valueOf(location.getLongitude());
			Log.v("Latitude " + location.getLatitude());
			Log.v("Longitude " + location.getLongitude());
		}
		nameValuePairs.put("login", mLogin);
		nameValuePairs.put("passwordhash", mPasswordhash);
		nameValuePairs.put("longitude", longitude);
		nameValuePairs.put("latitude", latitude);
		nameValuePairs.put("carmodel_id", carmodel_id);
		nameValuePairs.put("carmodel", carmodel);
		nameValuePairs.put("desc",
				String.format("%s %s %s", desc, latitude, longitude));
		nameValuePairs.put("gosnomer", gosnomer);
		nameValuePairs.put("nogosnomer", nonomer);
		String responseString = SendDataPost(mHost + "/addfact",
				nameValuePairs, files);
		Log.v(responseString);
		String result = _parseXML(responseString, "callresult");
		if (result.equalsIgnoreCase("ok")) {
			return this.getString(R.string.add_fact_ok);
			// Toast.makeText(this.getApplicationContext(),
			// this.getString(R.string.add_fact_ok),
			// Toast.LENGTH_SHORT).show();
		} else if (result.equalsIgnoreCase("fail")) {
			return this.getString(R.string.add_fact_fail);
			// Toast.makeText(this.getApplicationContext(),
			// this.getString(R.string.add_fact_fail),
			// Toast.LENGTH_SHORT).show();
		} else {
			_parseXML(responseString, "error", "code");
			return mLastErrorText;
			// Toast.makeText(this.getApplicationContext(), mLastErrorText,
			// Toast.LENGTH_SHORT).show();
		}
	}

	public Location get_location() {
		Location location = null;// = getExifLocation(path.getPath());
		if (location == null) {
			// Получение гео данных из системы.
			Location gps = ((LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE))
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			Location net = ((LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE))
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			// Определение лучших данных.
			if (gps != null && net != null) {
				if (gps.getTime() > net.getTime())
					location = gps;
				else if (net.hasAccuracy()) {
					float[] results = new float[1];
					Location.distanceBetween(net.getLatitude(),
							net.getLongitude(), gps.getLatitude(),
							gps.getLongitude(), results);
					if (results[0] < net.getAccuracy())
						location = gps;
				}
			} else if (net == null)
				location = gps;
			else if (gps == null)
				location = net;
		}
		Log.v("get_location " + location);
		return location;
	}

	public void _login() {
		mLogin = mSettings.getString(this.getString(R.string.loginKey), "");
		mPassword = mSettings.getString(
				this.getString(R.string.passwordKey), "");
		Log.v("Login..." + mLogin + "=" + mPassword);
		// Add your data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("login", mLogin));
		nameValuePairs.add(new BasicNameValuePair("password", mPassword));

		String responseString = SendDataPost(
				this.getString(R.string.authorize), nameValuePairs);
		Log.v(responseString);
		// Разобрать XML и получить passwordhash
		mPasswordhash = _parseXML(responseString, "passwordhash");
		isLogin = mPasswordhash.equals("") ? false : true;
	}

	public void _checkAuth() {
		String login = mSettings.getString(
				this.getString(R.string.loginKey), "");
		// Add your data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("login", login));
		nameValuePairs.add(new BasicNameValuePair("passwordhash", mPasswordhash));
		String responseString = SendDataPost(this.getString(R.string.checkauth), nameValuePairs);
		Log.v(responseString);
		if (!_parseXML(responseString, "checkauthresult").equalsIgnoreCase("ok")) {
			_login();
		}
	}

	private String _parseXML(String xml, String tagname) {
		return _parseXML(xml, tagname, "");
	}

	private String _parseXML(String xml, String tagname, String attr) {
		// xml =
		// "<?xml version=\"1.0\" encoding=\"UTF-8\"?><autochmoreply><requesttime>1319720434</requesttime><requestmethod>POST</requestmethod><replytime>1319720434</replytime><user id=\"18\"><username full=\"username_full\"><name>_name_</name><secondname></secondname><lastname>lastname</lastname></username><passwordhash>4vil6zy3b03b622bd18cc1c614ae3d4ea5d048a5</passwordhash></user></autochmoreply>";
		InputStream inputStream;
		inputStream = new ByteArrayInputStream(xml.getBytes());
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(inputStream);
			Element root = dom.getDocumentElement();
			NodeList items = root.getElementsByTagName(tagname);
			if (items.getLength() == 0) {
				return "";
			}
			Node item = items.item(0);
			Log.v(tagname + " " + attr);
			if (!attr.equals("")) {
				mLastErrorCode = item.getAttributes().getNamedItem(attr)
						.getNodeValue();
				mLastErrorText = item.getFirstChild().getNodeValue();
				return "";
			} else {
				return item.getFirstChild().getNodeValue();
			}
		} catch (ParserConfigurationException e) {
			Log.v("ParserConfigurationException" + e.getMessage());
		} catch (SAXException e) {
			Log.v("SAXException" + e.getMessage());
		} catch (IOException e) {
			Log.v("IOException" + e.getMessage());
		}
		return "";
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo = cm.getActiveNetworkInfo();
		if (nInfo != null && nInfo.isConnected()) {
			return true;
		} else {
			Log.v("INTERNET IS UNAVAILABLE!");
			return false;
		}
	}

	public String SendDataPost(String url, Map<String, String> nameValuePairs,
			Map<String, String> files) {
		if (!isOnline())
			return null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpquery = new HttpPost(url);
		String responseString = "";
		if (nameValuePairs != null && files != null) {
			MultipartEntity multipartEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			// Не следует передавать в конструктор boundary и charset.
			try {
				for (Entry<String, String> pair : nameValuePairs.entrySet())
					multipartEntity.addPart(
							pair.getKey(),
							new StringBody(pair.getValue(), Charset
									.forName(HTTP.UTF_8)));
				for (Entry<String, String> entry : files.entrySet())
					multipartEntity.addPart(entry.getKey(), new FileBody(
							new File(entry.getValue()), "image/jpeg"));
				httpquery.setEntity(multipartEntity);
				HttpResponse response = httpclient.execute(httpquery);
				responseString = ReadResponse(response);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
			httpclient = null;
			httpquery = null;
			return responseString;
		}
		return "";
	}

	public String SendDataPost(String url, List<NameValuePair> nameValuePairs) {
		if (!isOnline())
			return null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpquery = new HttpPost(url);
		String responseString = "";
		try {
			if (nameValuePairs != null) {
				httpquery.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						HTTP.UTF_8));
			}

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httpquery);
			responseString = ReadResponse(response);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		httpclient = null;
		httpquery = null;
		// new FileBody(new File(""), "image/jpeg");
		return responseString;
	}

	public String SendDataGet(String url, List<NameValuePair> nameValuePairs) {
		if (!isOnline())
			return null;
		String params = "?";
		if (nameValuePairs != null) {
			for (NameValuePair nv : nameValuePairs) {
				params += String.format("%s=%s&", nv.getName(), nv.getValue());
			}
		}
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpquery = new HttpGet(url + params);
		String responseString = "";
		try {
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httpquery);
			responseString = ReadResponse(response);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		httpclient = null;
		httpquery = null;
		return responseString;
	}

	public String ReadResponse(HttpResponse response) {
		HttpEntity entity = response.getEntity();
		BufferedReader reader = null;
		String responseString = "";
		try {
			if (entity != null) {
				InputStream is = entity.getContent();
				if (is != null) {
					StringBuilder sb = new StringBuilder();
					String line;
					try {
						reader = new BufferedReader(new InputStreamReader(is,
								"UTF-8"));
						while ((line = reader.readLine()) != null) {
							sb.append(line);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
					} finally {
						if (is != null) {
							is.close();
						}
						if (reader != null) {
							reader.close();
						}
					}
					responseString = sb.toString();
				}
				entity.consumeContent();
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseString;
	}

}
