package com.kh.objet.users.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.kh.objet.objet.model.vo.Objet;
import com.kh.objet.reportudetail.model.vo.ReportUDetail;
import com.kh.objet.support.model.service.SupportService;
import com.kh.objet.support.model.vo.ApplySupport;
import com.kh.objet.users.model.service.UserManagementService;
import com.kh.objet.users.model.vo.LoginCount;
import com.kh.objet.users.model.vo.UserManagement;

@Controller
public class UserManagementController {

	private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);
	
	@Autowired
	private UserManagementService usermService;
	@Autowired
	private LoginCount logincService;
	@Autowired
	private SupportService supportService;

	public UserManagementController() {
	}

	@RequestMapping("userm.do")
	public String userList(Model model, HttpServletRequest request) {
		Map<String, String> map = new HashMap<>();
		   String usertype = request.getParameter("usertype");
		   String blackyn = request.getParameter("blackyn");
		   String quityn = request.getParameter("quityn");
		   String userid = request.getParameter("userid");
			String order = request.getParameter("order");
			String nickname = request.getParameter("nickname");
				map.put("blackyn", blackyn);
				map.put("quityn", quityn);
				map.put("usertype", usertype);
				map.put("order", order);
				map.put("nickname", nickname);
				map.put("userid", userid);
		int currentPage = 1;
		if(request.getParameter("page") != null) {
			currentPage = Integer.parseInt(request.getParameter("page"));
		}
		
		int limit = 10;  //한 페이지에 출력할 목록 갯수
		int listCount = usermService.selectUserListCount(map);  //테이블의 전체 목록 갯수 조회
		//총 페이지 수 계산
		int maxPage = listCount / limit;
		if(listCount % limit > 0)
			maxPage++;
		
		//currentPage 가 속한 페이지그룹의 시작페이지숫자와 끝숫자 계산
		//예, 현재 34페이지이면 31 ~ 40 이 됨. (페이지그룹의 수를 10개로 한 경우)
		int beginPage = (currentPage / limit) * limit + 1;
        if(currentPage % limit == 0) {
            beginPage -= limit;
         }
		int endPage = beginPage + 9;
		if(endPage > maxPage)
			endPage = maxPage;
		
		//currentPage 에 출력할 목록의 조회할 행 번호 계산
		int startRow = (currentPage * limit) - 9;
		int endRow = currentPage * limit;
		map.put("startRow", Integer.toString(startRow));
		map.put("endRow", Integer.toString(endRow));
		ArrayList<UserManagement> ulist = (ArrayList<UserManagement>) usermService.selectUserOrder(map);
			model.addAttribute("ulist", ulist);
			model.addAttribute("currentPage", currentPage);
			model.addAttribute("listCount", listCount);
			model.addAttribute("maxPage", maxPage);
			model.addAttribute("beginPage", beginPage);
			model.addAttribute("endPage", endPage);
			model.addAttribute("blackyn", blackyn);
			model.addAttribute("quityn", quityn);
			model.addAttribute("usertype", usertype);
			model.addAttribute("order", order);
			model.addAttribute("nickname", nickname);
			model.addAttribute("userid", userid);
			return "admin/userManagement";
	}
	@RequestMapping("userbk.do")
	public ModelAndView Blacklist(ModelAndView mv) {
		ArrayList<UserManagement> bklist = (ArrayList<UserManagement>) usermService.selectBlacklist();
		if (bklist != null) {
			mv.addObject("bklist", bklist);
			mv.setViewName("admin/userBlacklist");
		} else {
			mv.addObject("message", "블랙리스트 목록 조회 실패");
			mv.setViewName("common/error");
		}
		return mv;
	}
	@RequestMapping(value="userbkorder.do", method=RequestMethod.POST)
	public void BlacklistOrder(String order, HttpServletResponse response) throws IOException {
		ArrayList<UserManagement> bklist = (ArrayList<UserManagement>) usermService.selectBlacklistOrder(order);
		//전송용 json 객체
				JSONObject sendJson = new JSONObject();
				//json 배열 객체
				JSONArray jarr = new JSONArray();
				//list를 jarr 로 옮겨 저장 (복사)
				for( UserManagement userbk : bklist) {
					
				JSONObject job = new JSONObject();
				job.put("userid", userbk.getUserid());
				job.put("username", URLEncoder.encode(userbk.getUsername(), "utf-8"));
				job.put("nickname", URLEncoder.encode(userbk.getNickname(), "utf-8"));
				job.put("blackstart", userbk.getBlackstart().toString());
				job.put("blackend", userbk.getBlackend().toString());
				jarr.add(job);
				}
				
				sendJson.put("list", jarr);
				logger.debug(jarr.toJSONString());
				response.setContentType("application/jsonl charset=utf-8");
				PrintWriter out = response.getWriter();
				out.println(sendJson.toJSONString());
				out.flush();
				out.close();
				
	}
	
	@RequestMapping("usermd.do")
	public ModelAndView userMangeDetail(ModelAndView mv, @RequestParam("userid") String userid) {
		UserManagement usermd = usermService.selectUserDetail(userid);
		ArrayList<Objet> userob = (ArrayList<Objet>) usermService.selectUserObjet(userid);
		ArrayList<ReportUDetail> userrp = (ArrayList<ReportUDetail>) usermService.selectUserReport(userid);
		ArrayList<ReportUDetail> userrpt = (ArrayList<ReportUDetail>) usermService.selectUserReportTotal(userid);
		  if(usermd != null) {
		         mv.addObject("usermd", usermd);
		         mv.addObject("userob", userob);
		         mv.addObject("userrp", userrp);
		         mv.addObject("userrpt", userrpt);
		         mv.setViewName("admin/userManageDetail");
		      }else {
		         mv.addObject("message", "회원 정보 보기 실패!");
		         mv.setViewName("common/error");
		      }
		      return mv;
	}
	
	@RequestMapping(value="insertblack.do", method=RequestMethod.POST)
	public void insertBlackList(String order, HttpServletResponse response, HttpServletRequest request) throws IOException {
		Map<String, String> map = new HashMap<>();
		String[] useridArray = request.getParameterValues("userid");
		int result = 0, result2 = 0, result3 = 0;
		for(String userid : useridArray) {
			map.put("userid", userid);
			map.put("blackend", request.getParameter("blackend"));
			map.put("blackreason", request.getParameter("blackreason"));
			result2 = usermService.updateBlackYN(userid);
			if(usermService.selectBlacklist().toString().contains(userid)) {
				result3 = usermService.updateBlackDate(map);
			}else {
				result = usermService.insertBlackList(map);
			}
		}
		PrintWriter out = response.getWriter();
		if(result > 0 && result2 > 0 || result2 > 0 && result3 > 0) {
			out.append("success");
		}else {
			out.append("fail");
		}
		out.flush();
	}
	
	@RequestMapping(value="adminquit.do", method=RequestMethod.POST)
	public void updateQuitYN(String order, HttpServletResponse response, HttpServletRequest request) throws IOException {
		Map<String, String> map = new HashMap<>();
		String[] useridArray = request.getParameterValues("userid");
		int result = 0, result2 = 0;
		for(String userid : useridArray) {
			map.put("userid", userid);
			map.put("quitreason", request.getParameter("quitreason"));
			result = usermService.updateQuitYN(map);
			result2 = usermService.insertQuit(map);
		}
		PrintWriter out = response.getWriter();
		if(result > 0 && result2 > 0) {
			out.append("success");
		}else {
			out.append("fail");
		}
		out.flush();
	}
	
	@RequestMapping(value="blackend.do", method=RequestMethod.POST)
	public void deleteBlackList(String order, HttpServletResponse response, HttpServletRequest request) throws IOException {
		Map<String, String> map = new HashMap<>();
		String[] useridArray = request.getParameterValues("userid");
		int result = 0, result2 = 0;
		for(String userid : useridArray) {
			map.put("userid", userid);
			result = usermService.deleteBlackList(userid);
			result2 = usermService.updateBlackEnd(userid);
		}
		PrintWriter out = response.getWriter();
		if(result > 0 && result2 > 0) {
			out.append("success");
		}else {
			out.append("fail");
		}
		out.flush();
		out.close();
	}
/*	@RequestMapping(value="userorder.do", method=RequestMethod.POST)
	public void selectUserOrder(HttpServletResponse response, HttpServletRequest request) throws IOException {
		Map<String, String> map = new HashMap<>();
		int currentPage = 1;
		if(request.getParameter("page") != null) {
			currentPage = Integer.parseInt(request.getParameter("page"));
		}
		int limit = 10;  //한 페이지에 출력할 목록 갯수
		int listCount = usermService.selectUserListCount();  //테이블의 전체 목록 갯수 조회
		//총 페이지 수 계산
		int maxPage = listCount / limit;
		if(listCount % limit > 0)
			maxPage++;
		
		//currentPage 가 속한 페이지그룹의 시작페이지숫자와 끝숫자 계산
		//예, 현재 34페이지이면 31 ~ 40 이 됨. (페이지그룹의 수를 10개로 한 경우)
		int beginPage = (currentPage / limit) * limit + 1;
        if(currentPage % limit == 0) {
            beginPage -= limit;
         }
		int endPage = beginPage + 9;
		if(endPage > maxPage)
			endPage = maxPage;
		
		//currentPage 에 출력할 목록의 조회할 행 번호 계산
		int startRow = (currentPage * limit) - 9;
		int endRow = currentPage * limit;
		map.put("startRow", Integer.toString(startRow));
		map.put("endRow", Integer.toString(endRow));
		String order = request.getParameter("order");
		String usertype = request.getParameter("usertype");
		if(usertype.equals("all")) {
			map.put("all", usertype);
		}else if (usertype.equals("blackyn")) {
			map.put("blackyn", usertype);
		}else if (usertype.equals("quityn")) {
			map.put("quityn", usertype);
		}else {
			map.put("usertype", usertype);
		}
		map.put("order", order);
		logger.debug(order);
		logger.debug(usertype);
		
		ArrayList<UserManagement> userorderlist = (ArrayList<UserManagement>) usermService.selectUserOrder(map);
		logger.debug(userorderlist.toString());
		//전송용 json 객체
				JSONObject sendJson = new JSONObject();
				//json 배열 객체
				JSONArray jarr = new JSONArray();
				//list를 jarr 로 옮겨 저장 (복사)
					for (UserManagement userorder : userorderlist) {
						JSONObject job = new JSONObject();
						job.put("userid", userorder.getUserid());
						job.put("username", URLEncoder.encode(userorder.getUsername(), "utf-8"));
						job.put("nickname", URLEncoder.encode(userorder.getNickname(), "utf-8"));
						job.put("enrolldate", userorder.getEnrolldate().toString());
						job.put("quityn", userorder.getQuityn().toString());
						job.put("reportcount", userorder.getReportcount());
						job.put("blackyn", userorder.getBlackyn());
						job.put("reportc", userorder.getReportcount());
						jarr.add(job);
					}
				sendJson.put("list", jarr);
			//	logger.debug(jarr.toJSONString());
				response.setContentType("application/jsonl charset=utf-8");
				PrintWriter out = response.getWriter();
				out.println(sendJson.toJSONString());
				out.flush();
				out.close();
				
	}*/
	@RequestMapping("userInfo.do")
	public String userInfo(@RequestParam("userid") String userid, Model model) {
		UserManagement userinfo = usermService.selectUserDetail(userid);
		String view = "";
		if(userinfo != null) {
			model.addAttribute("userinfo", userinfo);
			view = "admin/UserInfoPopup";
		}else {
			model.addAttribute("message", "회원정보 조회 실패");
			view ="common/error";
		}
		return view;
	}
	
}