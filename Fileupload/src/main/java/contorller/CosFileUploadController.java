package contorller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.face.FileService;
import service.impl.FileServiceImpl;

@WebServlet("/cos/fileupload")
public class CosFileUploadController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	//서비스
	private FileService fileService = new FileServiceImpl();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("/cos/fileupload [GET]");
	
		req.getRequestDispatcher("/WEB-INF/views/cos/fileupload.jsp").forward(req, resp);

		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("/cos/fileupload [POST]");
	
		if(!fileService.cosFileupload(req)) {
			
			//COS 파일 업로드 실패
			req.getRequestDispatcher("/WEB-INF/views/cos/error.jsp").forward(req, resp);
			return;
			
		} 
		
		resp.sendRedirect("/file/list");
	
	}

}
