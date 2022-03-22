package controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import beans.Reply;
import dao.ReplyDAO;


@WebServlet("/replyController")
public class ReplyController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private ReplyDAO replyDao;
	
	@Resource(name = "jdbc/shop")
	private DataSource datasource;

    @Override
	public void init() throws ServletException {
		// 서블릿 컨테이터가 서블릿 객체를 생성한 후 맨 처음 딱 한번만 호출되는 메서드. ReplyDao의 datasource를 가져와 replyDao객체에 저장하여 사용
		replyDao = new ReplyDAO(datasource);
	}


	public ReplyController() {
        super();
    }



	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		request.setCharacterEncoding("UTF-8");
		String action = request.getParameter("cmd");
		
		if(action == null) {
			action = "find";
		}
		
		try {
			switch (action) {
			case "find":	// 덧글 찾기
				find(request, response);
				break;
			case "delete":	// 덧글 삭제
				delete(request, response);
				break;
			default:
				find(request, response);
				break;
			}
		} catch (Exception e) {
			System.out.println("잘못된 접근입니다.");
		}
	}

	// review테이블의 farmID와 현재 로그인한 famrID와 비교하는 부분은 이 서블릿의 각 메서드에서 만들어줄것. 우선은 확인해야하니 빼고만듦.
	// 클릭한 reviewID와 같은 하나의 덧글만 출력되므로 일반적인 경우의 find메서드와 동일하다고 생각하면 됨 => 기본형을 find로 수정하자
	
	private void find(HttpServletRequest request, HttpServletResponse response) {
		// 리뷰ID가 같은 reply테이블의 행이 있을 경우 덧글을 보여준다
		int id = Integer.parseInt(request.getParameter("replyID"));
		
		Reply reply = replyDao.find(id);
		if(reply != null) {
			System.out.println("덧글 찾기 완료");
			System.out.println(reply);
		}
	}

	private void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 덧글삭제버튼을 눌렀을때 덧글삭제메서드가 실행되도록
		int id = Integer.parseInt(request.getParameter("replyID"));	// replyID를 가져옴
		int review = Integer.parseInt(request.getParameter("id"));
		
		boolean isDeleted = replyDao.delete(id);
		if(isDeleted) {

			RequestDispatcher rd = request.getRequestDispatcher("reviewController?cmd=view&id=" + review);	// forward해주기 위해 RequestDispatcher로 리퀘스트를 유지함
			rd.forward(request, response);
			
			System.out.println("삭제왼료@@");
		}
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 덧글작성버튼을 눌렀을때 덧글작성메서드가 실행되도록
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		
		int review = Integer.parseInt(request.getParameter("id"));
		
		Reply reply = new Reply();

//		reply.setReplyID(Integer.parseInt(request.getParameter("replyID")));	// replyID는 직접 입력받는 값이 아님
		reply.setFarmID(request.getParameter("farmID"));
		reply.setReplyContent(request.getParameter("replyContent"));
		reply.setReviewID(Integer.parseInt(request.getParameter("reviewID")));
		
		boolean isSaved = replyDao.save(reply);	// true이면 저장성공, false이면 실패
		
		if(isSaved) {
			RequestDispatcher rd = request.getRequestDispatcher("reviewController?cmd=view&id=" + review);	// forward해주기 위해 RequestDispatcher로 리퀘스트를 유지함
			rd.forward(request, response);
			System.out.println("입력완료@@");
		}
	}
}

