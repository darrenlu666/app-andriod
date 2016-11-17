package com.codyy.erpsportal.commons.models.network;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.models.entities.ModuleConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class ConfigRequest extends Request<ModuleConfig> {

    private final static String TAG = "ConfigRequest";

	private Map<String, String> mMap;
	private Listener<ModuleConfig> mListener;
	private ConfigParser mConfigParser;

	public ConfigRequest(String url, Map<String, String> map,
                         ConfigParser configParser,
                         Listener<ModuleConfig> listener,
						 ErrorListener errorListener) {
		super(Method.POST, url, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(15000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mConfigParser = configParser;
		mListener = listener;
		mMap = map;
	}

	public ConfigRequest(String url, Map<String, String> map, Object tag,
                         ConfigParser configParser,
                         Listener<ModuleConfig> listener,
						 ErrorListener errorListener) {
		super(Method.POST, url, errorListener);
		setRetryPolicy(new DefaultRetryPolicy(15000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mConfigParser = configParser;
		mListener = listener;
		mMap = map;
        setTag(tag);
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mMap;
	}

	@Override
	protected Response<ModuleConfig> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
            Cog.d(TAG, "parseNetworkResponse jsonString=", jsonString);
			JSONObject jsonObject = new JSONObject(jsonString);
            ModuleConfig moduleConfig = null;
            if (mConfigParser != null) moduleConfig = mConfigParser.onParse(jsonObject);
			return Response.success(moduleConfig, HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

	@Override
	protected void deliverResponse(ModuleConfig response) {
		mListener.onResponse(response);
	}

    public interface ConfigParser {
        ModuleConfig onParse(JSONObject jsonObject);
    }
}
