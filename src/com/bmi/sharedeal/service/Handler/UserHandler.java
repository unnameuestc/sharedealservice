package com.bmi.sharedeal.service.Handler;

import com.bmi.sharedeal.service.DAO.User;
import com.bmi.sharedeal.service.Server.*;
import com.bmi.sharedeal.service.utils.TextUtils;

import org.nutz.dao.Cnd;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.util.Daos;

import java.util.*;

/**
 * 用户模块
 *
 * @author xiaoyu
 */
public class UserHandler extends BaseHandler {
    /**
     * 登录
     */
    public void api_doLogin() {
        String userName = (String) this.getArgument("username");  //可能是用户名
        String pwd = (String) this.getArgument("pwd");

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)) {
            this.writeError("参数不足，请填写用户名、密码");
            return;
        }

        User u = dao.fetch(User.class, Cnd.where(Cnd.exps("name", "=", userName)).and("password", "=", pwd));

        if (u == null) {
            this.writeError("用户名或密码错误");
            return;
        }
        
        //非首次登录
        if (u.getLoginTime() != null){
        	//生成authKey
            String seed = u.getName() + new Date().toString() + new Random().nextInt(1000);
            String md5 = TextUtils.MD5(seed);
            if (TextUtils.isEmpty(md5)) {
                this.writeError("服务器内部错误");
                return;
            }

            u.setAuthKey(md5);            
            if (updateUser(u, "^authKey$") <= 0) {
                this.writeError("数据库错误");
                return;
            }

            Map res = new HashMap();
            res.put("authKey", u.getAuthKey());
            res.put("user", u.toKVPair());
            this.writeResult(res);
        } else {	//首次登录，需要修改密码									
        	String newpwd1 = (String) this.getArgument("newpwd1");  //可能是用户名
            String newpwd2 = (String) this.getArgument("newpwd2");

            if (TextUtils.isEmpty(newpwd1) || TextUtils.isEmpty(newpwd2)) {
                this.writeError("参数不足，首次登录请填写新密码");
                return;
            }
            
            if (!newpwd1.equalsIgnoreCase(newpwd2)) {
            	this.writeError("两次输入新密码不一致");
            	return;
            }
                             
            //生成authKey
            String seed = u.getName() + new Date().toString() + new Random().nextInt(1000);
            String md5 = TextUtils.MD5(seed);
            if (TextUtils.isEmpty(md5)) {
                this.writeError("服务器内部错误");
                return;
            }

            u.setAuthKey(md5);
            u.setPassword(newpwd1);
            u.setLoginTime(new Date());
            System.out.println("Date:" + new Date());
            if (dao.update(u) <= 0) {	
                this.writeError("数据库错误");
                return;
            }

            Map res = new HashMap();
            res.put("authKey", u.getAuthKey());
            res.put("user", u.toKVPair());
            this.writeResult(res);
        } 
        
    }

    /**
     * 退出登录
     */
    public void api_doLogout() {
        User user = checkUserAuth(this);
        if (user == null) {
            return;
        }

        user.setAuthKey(null);

        if (updateUser(user, "^authKey$") <= 0) {
            this.writeError("数据库错误");
            return;
        }

        this.writeResult(null);
    }

    /**
     * 获取用户信息(不指定用户名/id则为获取自身信息),优先级：id > name > authKey
     */
    public void api_getInfo(){
        User user = null;

        String id = (String) this.getArgument("id");
        if(!TextUtils.isEmpty(id)){
            user = dao.fetch(User.class, Cnd.where("id", "=", id));
            if (user == null) {
                this.writeError("用户不存在");
                return;
            }
        }else {
            String name = (String) this.getArgument("name");
            if (!TextUtils.isEmpty(name)) {
                user = dao.fetch(User.class, Cnd.where("name", "=", name));
                if (user == null) {
                    this.writeError("用户不存在");
                    return;
                }
            }else{
                user = checkUserAuth(this);
                if (user == null) {
                    return;
                }
            }
        }

        Map res = new HashMap();
        res.put("user", user.toKVPair());
        this.writeResult(res);
    }

    public void api_getAllUsers(){
        String page = (String) this.getArgument("page");
        if (TextUtils.isEmpty(page)) {
            page = "1";
        }

        List<User> users = dao.query(User.class, Cnd.NEW().asc("id"),
                dao.createPager(Integer.parseInt(page), 10));

        //总用户数
        int allCnt = dao.count(User.class, null);

        Map res = new HashMap();
        res.put("allCnt", allCnt);
        res.put("cnt", users.size());
        res.put("users", users);
        res.put("page", page);

        this.writeResult(res);
    }

    /**
     * 通用方法，检查授权情况
     */
    public static User checkUserAuth(BaseHandler h) {
        String authKey = (String) h.getArgument("authkey");
        if (TextUtils.isEmpty(authKey)) {
            h.writeError("参数不足");
            return null;
        }

        User u = h.dao.fetch(User.class, Cnd.where("authkey", "=", authKey));
        if (u == null) {
            h.writeError("用户验证失败");
            return null;
        }

        return u;
    }

    /**
     * 只更新需要的字段
     * @param user
     * @param actived
     * @return
     */
    private int updateUser(User user, String actived){
        return Daos.ext(dao, FieldFilter.create(User.class, actived)).update(user);
    }
}
