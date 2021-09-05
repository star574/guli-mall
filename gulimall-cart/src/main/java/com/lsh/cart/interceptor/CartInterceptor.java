package com.lsh.cart.interceptor;


import com.lsh.cart.vo.UserInfoTo;
import com.lsh.gulimall.common.constant.AuthServerConstant;
import com.lsh.gulimall.common.constant.CartConstant;
import com.lsh.gulimall.common.vo.MemberRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * //TODO
 *
 * @Author shihe
 * @Date 1:33 2021/8/9
 * @Description 拦截器 在执行目标方法之前 判断用户登录状态 并封装专递给Controller
 **/
@Slf4j
@Component
public class CartInterceptor implements HandlerInterceptor {

	/*同一个线程共享数据*/
	public static ThreadLocal<UserInfoTo> toThreadLocal = new ThreadLocal<>();


	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/8/9 1:34
	 * @Description 目标方法执行之前
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();
		MemberRespVo attribute = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
		UserInfoTo userInfoTo = null;
		userInfoTo = new UserInfoTo();
		if (attribute != null) {
			/*已经登录 设置id 放行 直接放行*/
			userInfoTo.setUserId(attribute.getId());
			/*同一个线程共享数据 在controller获取*/
		}
		/*判断cookie中是否存在user-key*/
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				String name = cookie.getName();
				if (name.equals(CartConstant.TEMP_USER_COOKIE_NAME)) {
					log.info("获取临时用户成功! " + cookie.getValue());
					userInfoTo.setUserKey(cookie.getValue());
					userInfoTo.setTempUser(true);
					/*同一个线程共享数据 在controller获取*/
					toThreadLocal.set(userInfoTo);
					return true;
				}

			}
		}
		log.info("userId:" + userInfoTo.getUserId() + "user-key" + userInfoTo.getUserKey());
		if ((userInfoTo.getUserId() == null || userInfoTo.getUserId() == 0) && StringUtils.isEmpty(userInfoTo.getUserKey())) {
			/*既没有登录也没有临时用户 分配临时用户*/
			log.info("分配临时用户");
			String userKey = UUID.randomUUID().toString().replace("-", "");
			userInfoTo.setUserKey(userKey);
			/*同一个线程共享数据 在controller获取*/
			toThreadLocal.set(userInfoTo);
			return true;
		}
		return false;
	}

	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/8/9 2:02
	 * @Description 业务执行之后
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		UserInfoTo userInfoTo = toThreadLocal.get();
		if (!StringUtils.isEmpty(userInfoTo.getUserKey())) {
			Cookie	cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, toThreadLocal.get().getUserKey());
			cookie.setDomain("gulimall.com");
			cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
			response.addCookie(cookie);
		}
		toThreadLocal.remove();
	}
}
