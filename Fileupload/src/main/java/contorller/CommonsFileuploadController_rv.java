package contorller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.face.FileServiceRv;
import service.impl.FileServiceImplRv;

@WebServlet("/commons/fileupload_rv")
public class CommonsFileuploadController_rv extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private FileServiceRv fileServiceRv = new FileServiceImplRv();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("/commons/fileupload_rv [GET]");
		
		//View 지정하고 포워드
		req.getRequestDispatcher("/WEB-INF/views/commons/fileupload_rv.jsp").forward(req, resp);	
	
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("/commons/fileupload_rv [POST]");
	
		//multipart-format 형식으로 요청하면 null값 반환 -> 파일 업로드 라이브러리가 필요
		System.out.println("title : " + req.getParameter("title"));
		
		//-------------------------------------------------------------
		
		//파일 업로드 처리
		boolean result = fileServiceRv.fileupload(req);
		
		System.out.println(result);
		
		if(!result) {
			req.getRequestDispatcher("/WEB-INF/views/commons/error_rv.jsp").forward(req, resp);
			
			return;
		}
		
		//!!!처리는 다 되는데 왜 DB에 안들어갈까..?
	
	}

}
