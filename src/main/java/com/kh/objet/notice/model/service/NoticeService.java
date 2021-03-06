package com.kh.objet.notice.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kh.objet.notice.model.vo.Notice;
import com.kh.objet.notice.model.vo.SearchDate;

public interface NoticeService {
	List<Notice> selectNoticeList(HashMap<String, Object> map);

	public List<Notice> selectNoticeListAsc();

	public int getListCount();

	public Notice selectNoticeDetail(int noticeno);

	public int noticeSearchListCount(HashMap<String, Object> mapp);

	public List<Notice> selectNoticeSearchList(HashMap<String, Object> map);

	public int insertNotice(Notice notice);

	public int updateNotice(Notice notice);

	public int deleteNotice(int noticeno);

	public int deleteFile(Notice notice);

	public ArrayList<Notice> selectNoticeListAd();

	 public List<Notice> selectNoticeListAd(Map<String, String> map);

	   public int selectNoticeCountAd(Map<String, String> map);

	   public Notice selectNextPrevNotice(int noticeno);
	   
	   public int updateNoticeAd(Notice notice);
	   
	   public int deleteNoticeAd(int noticeno);

	   public int insertNoticeAd(Notice notice);

	   List<Notice> selectNoticeSearchAd(Map<String, String> map);

	   int selectNoticeSearchAdCount(Map<String, String> map);

	List<Notice> selectNoticeType();

	List<Notice> selectNoticeType1();

	List<Notice> selectNoticeType2();

	List<Notice> selectNoticeType3();





}
