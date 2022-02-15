package mvcBoard.command;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvcBoard.dao.BDao;
import mvcBoard.dto.BDto;

public class BListCommand implements BCommand {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		// 게시글 불러오기
		BDao dao = new BDao();
		ArrayList<BDto> dtos = dao.list();//db에서 dto 객체들을 select해온다.
		request.setAttribute("list", dtos);
	}

}
