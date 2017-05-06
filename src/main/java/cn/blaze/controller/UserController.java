package cn.blaze.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.blaze.domain.UserInfo;
import cn.blaze.service.MailService;
import cn.blaze.service.StudentInfoService;
import cn.blaze.service.UserInfoService;
import cn.blaze.utils.BlazeConstants;
import cn.blaze.utils.CommonUtils;
import cn.blaze.vo.UserInfoVo;

/**
 * @ClassName UserController
 * @Description 用户登录
 * @author LiuLei
 * @date 2017年4月17日 下午7:44:54
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController{
	@Autowired
	private UserInfoService userInfoService;
	@Autowired 
	private StudentInfoService studentInfoService;
	@Autowired
	private MailService mailService;
	/**
	 * @Title forwardQueryUserInfo
	 * @Description：跳转到用户信息列表
	 * @param type 用户的类型 admin-管理员,student-学生
	 * @param request
	 * @return
	 * @user LiuLei 2017年4月28日
	 * @updater：
	 * @updateTime：
	 */
	@RequestMapping("forwardQueryUserInfo")
	public String forwardQueryUserInfo(String type, HttpServletRequest request){
		if("admin".equals(type)){
			return "userInfo/userInfo_admin";
		}else {
			return "userInfo/userInfo_student";
		}
	}
	
	/**
	 * @Title queryAllUserStudentInfo
	 * @Description：查询所有用户学生信息
	 * @param type 用户的类型 admin-管理员,student-学生
	 * @param request
	 * @return
	 * @user LiuLei 2017年4月26日
	 * @updater：
	 * @updateTime：
	 */
	@ResponseBody
	@RequestMapping("queryUserInfoJson")
	public String queryUserInfoJson(String type, HttpServletRequest request){
		Map<String, Object> map = new HashMap<String, Object>();
		if("admin".equals(type)){
			map.put("type",BlazeConstants.USER_TYPE_ADMIN);
		}else {
			map.put("type",BlazeConstants.USER_TYPE_STUDENT);
		}
		String userName = this.getNotNullValue(request.getParameter("userName"));
		String studentName = this.getNotNullValue(request.getParameter("studentName"));
		String status = this.getNotNullValue(request.getParameter("status"));
		String isvalid = this.getNotNullValue(request.getParameter("isvalid"));
		map.put("userName", userName);
		map.put("studentName", studentName);
		map.put("status", status);
		map.put("isvalid", isvalid);
		
		String sortName = this.getNotNullValue(request.getParameter("sortname"));
		String sortOrder = this.getNotNullValue(request.getParameter("sortorder"));
		int page = this.getNotNullValueToInt(request.getParameter("page"));
		int size = this.getNotNullValueToInt(request.getParameter("pagesize"));
		return studentInfoService.queryUserStudentInfoByParameterForLigerUI(map, sortName, sortOrder, page, size);
	}
	
	/**
	 * @Title resetUserPasswordById
	 * @Description：根据用户id重置用户密码
	 * @param request
	 * @return
	 * @user LiuLei 2017年4月28日
	 * @updater：
	 * @updateTime：
	 */
	@ResponseBody
	@RequestMapping("resetUserPasswordById")
	public Map<String, Object> resetUserPasswordById(HttpServletRequest request){
		String id = getNotNullValue(request.getParameter("id"));
		UserInfo loginUser = this.getLoginUser(request);
		// 判断是否是管理员,管理员才具有重置密码权限
		if(!BlazeConstants.USER_TYPE_ADMIN.equals(loginUser.getType())){// 不是管理员无权重置
			return buildJsonMap("fail", "不是管理员无权操作");
		}
		userInfoService.resetuserPasswordById(id);
		return buildJsonMap("success", "");
	}
	
	/**
	 * @Title cancelUser
	 * @Description：注销用户
	 * @param request
	 * @return
	 * @user LiuLei 2017年4月28日
	 * @updater：
	 * @updateTime：
	 */
	@ResponseBody
	@RequestMapping("changeUserValidById")
	public Map<String, Object> changeUserValidById(HttpServletRequest request){
		String id = getNotNullValue(request.getParameter("id"));
		String isvalid = getNotNullValue(request.getParameter("isvalid"));
		
		if(!BlazeConstants.USER_TYPE_ADMIN.equals(this.getLoginUser(request).getType())){// 不是管理员无权注销
			return buildJsonMap("fail", "不是管理员无权操作");
		}
		
		if(BlazeConstants.ISVALID_NO.equals(isvalid)){
			userInfoService.cancelUserById(id);
		}else if(BlazeConstants.ISVALID_YES.equals(isvalid)){
			userInfoService.enableUserById(id);
		}
		return buildJsonMap("success", "");
	}
	
	/**
	 * @Title adminLogin
	 * @Description：跳转到管理员用户登录界面
	 * @param userInfo
	 * @param request
	 * @return
	 * @user LiuLei 2017年4月26日
	 * @updater：
	 * @updateTime：
	 */
	@RequestMapping("admin")
	public String adminLogin(HttpServletRequest request){
		return "index/adminLogin";
	}
	
	/**
	 * @Title login
	 * @Description：用户登录
	 * @param request
	 * @return
	 * @user LiuLei 2017年4月17日
	 * @updater：
	 * @updateTime：
	 */
	@RequestMapping("login")
	public String login(UserInfo userInfo, HttpServletRequest request){
		String userName = userInfo.getUserName()!=null?userInfo.getUserName():"";
		String password = userInfo.getPassword()!=null?userInfo.getPassword():"";
		String type = userInfo.getType()!=null?userInfo.getType():"";
		
		if(!"".equals(userName) && !"".equals(password)){
			
			Map<String, Object> map = new HashMap<String, Object>(2);
			map.put("userName", userName);
			map.put("password", password);
			map.put("type", type);
			CommonUtils.removeNullValue(map);
			UserInfo user = userInfoService.queryUserInfoByUserNameAndPassword(map);
			
			if(user != null){// 用户存在
				if(BlazeConstants.USER_TYPE_ADMIN.equals(type)){// 管理员登录
					this.saveLoginUser(request, user);// 保存用户信息到session
					return "index/adminIndex";
				}else {// 学生或者普通用户登录
					this.saveLoginUser(request, user);// 保存用户信息到session
					request.setAttribute("user", user);
					return "index/userIndex";
				}
			}
		}
		
		request.setAttribute("msg", "用户名或密码错误!");
		/***** 登录失败  *****/
		if(BlazeConstants.USER_TYPE_ADMIN.equals(type)){// 管理员登录
			return "index/adminLogin";
		}else {// 学生登录
			return "index/login";
		}
	}
	
	/**
	 * @Title forwardUpdatePassword
	 * @Description：跳转到修改密码页面
	 * @param userInfoVo
	 * @param request
	 * @return
	 * @user LiuLei 2017年4月24日
	 * @updater：
	 * @updateTime：
	 */
	@RequestMapping("forwardUpdatePassword")
	public String forwardUpdatePassword(HttpServletRequest request){
//		UserInfo user = (UserInfo) request.getSession().getAttribute("loginUser");
		UserInfo user = this.getLoginUser(request);
		request.setAttribute("userInfo", user);
		return "userInfo/update_password";
	}
	
	/**
	 * @Title updatePassword
	 * @Description：修改用户密码
	 * @param userInfoVo
	 * @param request
	 * @return
	 * @user LiuLei 2017年4月24日
	 * @updater：
	 * @updateTime：
	 */
	@ResponseBody
	@RequestMapping("updatePassword")
	public Object updatePassword(UserInfoVo userInfoVo, HttpServletRequest request){
		UserInfo db_userInfo = userInfoService.queryUserInfoByStudentId(userInfoVo.getStudentId());
		String pwd = db_userInfo.getPassword();
		if(pwd!=null && pwd.equals(userInfoVo.getPassword())){
			db_userInfo.setPassword(userInfoVo.getNewPassword());
			db_userInfo.setUpdateTime(new Date());
			userInfoService.updateUserInfoById(db_userInfo);
			return buildJsonMap("success", null);
		}
		return buildJsonMap("密码错误,请重新输入正确密码!", null);
	}
	
	/**
	 * @Title userRegister
	 * @Description：用户注册
	 * @param userInfo
	 * @param request
	 * @return
	 * @user LiuLei 2017年5月4日
	 * @updater：
	 * @updateTime：
	 */
	@RequestMapping("userRegister")
	public String userRegister(UserInfo userInfo, HttpServletRequest request){
		userInfo.setCreateTime(new Date());
		userInfo.setIsvalid(BlazeConstants.ISVALID_YES);
		userInfo.setType(BlazeConstants.USER_TYPE_COMMON);
		userInfo.setStatus("0");
		userInfoService.userRegister(userInfo);
		return "index/login";
	}
}
