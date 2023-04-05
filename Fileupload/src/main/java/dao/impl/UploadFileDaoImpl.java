package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.JDBCTemplate;
import dao.face.UploadFileDao;
import dto.UploadFile;

public class UploadFileDaoImpl implements UploadFileDao {

	private PreparedStatement ps = null;
	private ResultSet rs = null;
	
	@Override
	public int insert(Connection conn, UploadFile uploadFile) {

		String sql = "";
		sql += "INSERT INTO uploadfile(fileno, origin_name, stored_name)";
		sql += " VALUES(uploadfile_seq.nextval, ?, ?)";
		
		int res = 0;
		
		try {
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, uploadFile.getOriginName());
			ps.setString(2, uploadFile.getStoredName());
			
			res = ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCTemplate.close(ps);
		}
		
		return res;
	}
	
	@Override
	public List<UploadFile> select(Connection conn) {

		String sql = "";
		sql += "SELECT fileno, origin_name, stored_name FROM uploadfile";
		sql += " ORDER BY fileno DESC";
		
		List<UploadFile> list = new ArrayList<>();
		
		try {
			ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
			
			while(rs.next()) {
				
				UploadFile uploadFile = new UploadFile();
				
				uploadFile.setFileno(rs.getInt("fileno"));
				uploadFile.setOriginName(rs.getString("origin_name"));
				uploadFile.setStoredName(rs.getString("stored_name"));
				
				list.add(uploadFile);
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCTemplate.close(ps);
			JDBCTemplate.close(rs);
		}
		
		System.out.println("업로드파일 다오쪽 result : " + list);
		
		
		return list;
	}
	
}
