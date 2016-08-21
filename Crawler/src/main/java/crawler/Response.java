package crawler;

public class Response {

	private String content;
	private Exception exception;
	private int statusCode;
	
	
	public void setContent (String content){
		this.content = content;
	}
	
	public String getContent (){
		return this.content;
	}
	
	public void setStatusCode (int statusCode){
		this.statusCode = statusCode;
	}
	
	public void setException (Exception exception){
		this.exception = exception;
	}
	
	public boolean isSuccessful() {
		return (exception == null) && (content != null && content.trim().length() > 0) && statusCode != -1;
	}
	
	public Response(){
		this.statusCode = -1;
	}
	
	public Response (String content, Exception exception){
		this.content = content;
		this.exception = exception;
	}
	
}
