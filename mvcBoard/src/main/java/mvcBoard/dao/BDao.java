package mvcBoard.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import mvcBoard.dto.BDto;


public class BDao {
	
	DataSource dataSource;
	
	public BDao() {
		try {
			Context context = new InitialContext();
			dataSource = (DataSource) context.lookup("java:comp/env/jdbc/Oracle11g");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void write(String bName, String bTitle, String bContent) {
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = dataSource.getConnection();
			String query = "INSERT INTO mvc_board (bId, bName, bTitle, bContent, bHit, bGroup, bStep, bIndent)"
					+ " VALUES (mvc_board_seq.nextval, ?, ?, ?, 0, mvc_board_seq.currval, 0, 0)";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, bName);
			preparedStatement.setString(2, bTitle);
			preparedStatement.setString(3, bContent);
			int rn = preparedStatement.executeUpdate();
			//https://url.kr/adqocx
			//INSERT / DELETE / UPDATE : returns the number of reflected.
			//CREATE / DROP : return -1.
			
		}catch(Exception e) {
			e.printStackTrace();
			
		}finally {
			try {
				if(preparedStatement != null) preparedStatement.close();
				if(connection != null) connection.close();
				
			}catch(Exception e){//TODO
				e.printStackTrace();
			}
		}
	}

	public ArrayList<BDto> list() {
		ArrayList<BDto> dtos = new ArrayList<BDto>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = dataSource.getConnection();
			String query = "SELECT bId, bName, bTitle, bContent, bDate, bHit, bGroup, bStep, bIndent"
					+ "FROM mvc_board ORDER BY bGroup DESC, bStep ASC";
			/*
			 *  bGroup DESC 댓글은 최신순
			 *  bStep ASC 대댓글은 댓글 다음으로 정렬
			 *  
			 * 게시글 (1)
			 * ==================
			 * ㄴ 댓글2 1/8 (1-1)
			 * 	ㄴ 대댓글 1/8 (1-2)
			 * -------------------
			 * ㄴ 댓글1 1/5 (1-1)
			 * 	ㄴ 대댓글 1/6 (1-2)
			 * 	 ㄴ 대대댓글 ?
			 * -------------------
			 * */
			
			preparedStatement = connection.prepareStatement(query);
			resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {//다음항목 없으면 false
				int bId = resultSet.getInt("bId");
				String bName = resultSet.getString("bName");
				String bTitle = resultSet.getString("bTitle");
				String bContent = resultSet.getString("bContent");
				Timestamp bDate = resultSet.getTimestamp("bDate");
				int bHit = resultSet.getInt("bHit");
				int bGroup = resultSet.getInt("bGroup");
				int bStep = resultSet.getInt("bStep");
				int bIndent = resultSet.getInt("bIndent");
				
				BDto dto = new BDto(bId, bName, bTitle, bContent, bDate, bHit,bGroup,bStep, bIndent);
				dtos.add(dto);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			try {
				if(resultSet != null) resultSet.close();
				if(preparedStatement != null) preparedStatement.close();
				if(connection != null) connection.close();
			}catch(Exception e2) {
				e2.printStackTrace();
			}
		}
		
		return dtos;
	}

	public BDto contentView(String selected_bId) {
		// TODO Auto-generated method stub
		upHit(selected_bId);//클릭한 게시물 조회수 증가해서 db업데이트
		
		BDto dto = null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = dataSource.getConnection();
			
			String query = "SELECT * FROM mvc_board WHERE bId = ?";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, Integer.parseInt(selected_bId));//....
			resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next()) {
				int bId = resultSet.getInt("bId");//selected bId 와 변수명 조심
				String bName = resultSet.getString("bName");
				String bTitle = resultSet.getString("bTitle");
				String bContent = resultSet.getString("bContent");
				Timestamp bDate = resultSet.getTimestamp("bDate");
				int bHit = resultSet.getInt("bHit");
				int bGroup = resultSet.getInt("bGroup");
				int bStep = resultSet.getInt("bStep");
				int bIndent = resultSet.getInt("bIndent");
				
				dto = new BDto(bId, bName, bTitle, bContent, bDate, bHit,bGroup,bStep, bIndent);
			}
			
		}catch(Exception e) {e.printStackTrace();}
		finally {
			try {
				if(resultSet != null) resultSet.close();
				if(preparedStatement != null) preparedStatement.close();
				if(connection!= null) connection.close();
			}catch(Exception e2) {e2.printStackTrace();}
		}
		return dto;
	}

	private void upHit(String bId) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = dataSource.getConnection();
			String query = "UPDATE mvc_board set bHit = bHit +1 WHERE bId = ?";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, bId);
			
			int rn = preparedStatement.executeUpdate();
			
		}catch(Exception e){
			e.printStackTrace();
			
		}finally {
			try {
				if(preparedStatement != null) preparedStatement.close();
				if(connection != null) connection.close();
				
				}catch(Exception e2) {e2.printStackTrace();
			}
			
		}
		
	}

}
