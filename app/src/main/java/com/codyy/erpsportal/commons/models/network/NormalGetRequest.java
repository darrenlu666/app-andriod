package com.codyy.erpsportal.commons.models.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class NormalGetRequest extends Request<JSONObject> {

	private Listener<JSONObject> mListener;

	private Map<String, String> mParams;
	/**
	 * get or post method
	 * @param url
	 * @param listener
	 * @param errorListener
	 */
	public NormalGetRequest(String url, Listener<JSONObject> listener, ErrorListener errorListener) {
		super(Method.GET, url, errorListener);
		this.mListener = listener;
	}

    /**
     * get or post method
     * @param url
     * @param params
     * @param listener
     * @param errorListener
     */
    public NormalGetRequest(String url, Map<String, String> params ,Listener<JSONObject> listener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.mListener = listener;
        this.mParams = params;
    }

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mParams;
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			return Response.success(new JSONObject(jsonString),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

	@Override
	protected void deliverResponse(JSONObject response) {
		mListener.onResponse(response);
	}

}
