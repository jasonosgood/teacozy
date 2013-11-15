package martini.model;

import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import martini.HTMLBuilder;


public abstract class 
	Page
extends 
	HTMLBuilder
{
	private String _uri = "/";
	
	public void setURI( String uri )
	{
		_uri = uri;
	}
	
	public String getURI() 
	{ 
		return _uri; 
	}
	
	public abstract void setUrlParams( Map<String,String> params );
	
	public HttpServletRequest _request = null;
	
	public HttpServletRequest getRequest() 
	{
		return _request;
	}
	
	public boolean hasParameters()
	{
		return _parameterMap != null;
	}
	
	public String getRequestParameter( String key )
	{
		String result = "";
		if( hasParameters() && _parameterMap.containsKey( key ))
		{
			result = _parameterMap.get( key )[0];
		}
		if( getRequest() == null ) return result;
		return result;
	}
	
	public boolean hasRequestParameter( String key )
	{
		return hasParameters() && _parameterMap.containsKey( key );
	}
	
	public HttpServletResponse _response = null;
	
	public HttpServletResponse getResponse()
	{
		return _response;
	}
	
	
	// TODO: Is this generic type correct?
	private Handler<Page> _handler = new Handler<Page>();
	
	public void setHandler( Handler<Page> handler )
	{
		if( handler == null )
		{
			handler = new Handler<Page>();
		}
		_handler = handler;
		_handler.setPage( this );
	}
	
	public Handler<Page> getHandler()
	{
		return _handler;
	}
	
	private Map<String,String[]> _parameterMap = null;
	
	public void init( HttpServletRequest request, HttpServletResponse response )
		throws ServletException, IOException
	{
		_request = request;
		_response = response;
		
		String method = getRequest().getMethod();
		if( "GET".equals( method ))
		{
			Map<String,String[]> map = getRequest().getParameterMap();
			if( map.size() > 0 )
			{

				_parameterMap = map;
			}
		}
		else if( "POST".equals( method ))
		{
			try
			{
				Reader reader = null;
				StringBuilder sb = new StringBuilder();
				try
				{
					reader = getRequest().getReader();
					int n;
					while ((n = reader.read()) != -1 )
					{
						sb.append( (char) n ); 
					}
					_parameterMap = extractMap( sb.toString() );
				}
				finally
				{
					reader.close();
					System.out.println( sb.toString() );
					System.out.println(" -- ");
				}
			}
			catch( Exception e )
			{
				//
			}
		}
	}
	
	public HashMap<String,String[]> extractMap( String payload )
	{
		try
		{
			HashMap<String,String[]> map = new HashMap<String,String[]>();
			String[] stuff = payload.split( "&" );
			for( String item : stuff )
			{
				String[] pair = item.split( "=" );
				String key = pair[0];
				key = URLDecoder.decode( key, "UTF-8" ).trim();
				
				if( pair.length > 1 )
				{
					String value = URLDecoder.decode( pair[1], "UTF-8" );
//					System.out.printf( "\n%s = %s", key, value );
					String[] values = new String[]{ value };
					map.put( key, values );
				}
			}
			return map;
		}
		catch( Exception e )
		{
			return null;
		}
	}

//	public void render( HttpServletRequest request, HttpServletResponse response )
//			throws ServletException, IOException {}

	public abstract void render( HttpServletResponse response ) throws ServletException, IOException;

//	public void afterHandle() throws Exception {}
	
	/**
	 *  Generated subclass overrides template method this. Used to transfer URI's 
	 *  query parameters to the Page instance.
	 */
	public abstract void populateForm();

	// Every HTML page has a title. 
	private String _title = null;
	
	public void setTitle( String title )
	{
		_title = title;
	}
	
	public String getTitle()
	{
		return _title;
	}
	
	// ID set by the Dispatcher
	private String _id = null;
	
	public void setID( String id )
	{
		_id = id;
	}
	
	public String getID()
	{
		return _id;
	}
	
	private long _elapsed;
	
	public void setElapsed( long elapsed )
	{
		_elapsed = elapsed;
	}
	
	public long getElapsed()
	{
		return _elapsed;
	}
}