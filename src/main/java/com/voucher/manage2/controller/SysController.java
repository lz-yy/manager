package com.voucher.manage2.controller;

import cn.hutool.core.util.IdUtil;
import com.voucher.manage.dao.CurrentDao;
import com.voucher.manage2.dto.SysModelDTO;
import com.voucher.manage2.dto.SysRoleDTO;
import com.voucher.manage2.dto.SysUserDTO;
import com.voucher.manage2.exception.BaseException;
import com.voucher.manage2.service.SysService;
import com.voucher.manage2.tkmapper.entity.*;
import com.voucher.manage2.utils.MapUtils;
import com.voucher.manage2.utils.ObjectUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author lz
 * @description
 * @date 2019/6/10
 */
@RestController
@RequestMapping("/sys")
public class SysController {
    @Autowired
    private SysService sysService;
    @Autowired
    private CurrentDao currentDao;

    private static final Pattern urlPattern = Pattern.compile("^/[a-zA-Z]+/[a-zA-Z]+\\.do$");

    @RequestMapping("addUrls")
    public Integer addUrls(@RequestBody Map<String, Object> jsonMap) {
        List<Map<String, Object>> urls = (List<Map<String, Object>>) jsonMap.get("urls");
        List<SysUrl> sysUrls = new ArrayList<>();
        for (Map<String, Object> url : urls) {
            String urlStr = MapUtils.getString("url", url);
            if (urlPattern.matcher(urlStr).find()) {
                SysUrl sysUrl = new SysUrl();
                sysUrl.setName(MapUtils.getString("name", url));
                sysUrl.setDescription(MapUtils.getString("description", url));
                sysUrl.setUrl(urlStr);
                sysUrls.add(sysUrl);
            } else {
                throw BaseException.getDefault("url非法,示例:/aa/bb.do");
            }
        }
        return sysService.addUrls(sysUrls);
    }

    @RequestMapping("getUrls")
    public List<SysUrl> getUrls(String urlCondition) {
        return sysService.getUrlsLikeCondition(urlCondition);
    }

    @RequestMapping("getUrlsByModel")
    public SysModelDTO getUrlsByModel(String modelGuid) {
        return sysService.getUrlsByModel(modelGuid);
    }

    @RequestMapping("addModel")
    public Integer addModel(@RequestBody Map<String, Object> jsonMap) {
        SysModel sysModel = new SysModel();
        sysModel.setGuid(IdUtil.simpleUUID());
        sysModel.setName(MapUtils.getString("name", jsonMap));
        sysModel.setDescription(MapUtils.getString("description", jsonMap));
        return sysService.addModel(sysModel);
    }

    @RequestMapping("updateModelUrls")
    public Integer updateModelUrls(@RequestBody Map<String, Object> jsonMap) {
        List<String> addUrls = MapUtils.getStrList("addUrls", jsonMap);
        List<String> delUrls = MapUtils.getStrList("delUrls", jsonMap);
        List<SysModelUrl> addModelUrls = new ArrayList<>();
        List<SysModelUrl> delModelUrls = new ArrayList<>();
        String modelGuid = MapUtils.getString("modelGuid", jsonMap);
        if (ObjectUtils.isNotEmpty(addUrls)) {
            for (String url : addUrls) {
                SysModelUrl sysModelUrl = new SysModelUrl();
                sysModelUrl.setModelGuid(modelGuid);
                sysModelUrl.setUrl(url);
                addModelUrls.add(sysModelUrl);
            }
        }
        if (ObjectUtils.isNotEmpty(delUrls)) {
            for (String url : delUrls) {
                SysModelUrl delSysModelUrl = new SysModelUrl();
                delSysModelUrl.setModelGuid(modelGuid);
                delSysModelUrl.setUrl(url);
                delModelUrls.add(delSysModelUrl);
            }
        }

        return sysService.updateModelUrls(addModelUrls, delModelUrls);
    }

    @RequestMapping("addRole")
    public Integer addRole(@RequestBody Map<String, Object> jsonMap) {
        SysRole sysRole = new SysRole();
        sysRole.setGuid(IdUtil.simpleUUID());
        sysRole.setName(MapUtils.getString("name", jsonMap));
        sysRole.setDescription(MapUtils.getString("description", jsonMap));
        return sysService.addRole(sysRole);
    }

    @RequestMapping("getModelsByRoleGuid")
    public SysRoleDTO getModelsByRoleGuid(String roleGuid) {
        return sysService.getModelsByRoleGuid(roleGuid);
    }

    @RequestMapping("getModelsLikeModelName")
    public List<SysModel> getModelsLikeModelName(String modelName) {
        return sysService.getModelsLikeModelName(modelName);
    }

    @RequestMapping("updateRoleModels")
    public Integer addRoleModels(@RequestBody Map<String, Object> jsonMap) {
        List<String> delModelGuids = MapUtils.getStrList("delModelGuids", jsonMap);
        List<String> addModelGuids = MapUtils.getStrList("addModelGuids", jsonMap);
        List<SysRoleModel> addroleModels = new ArrayList<>();
        List<SysRoleModel> delroleModels = new ArrayList<>();
        String roleGuid = MapUtils.getString("roleGuid", jsonMap);
        if (ObjectUtils.isNotEmpty(addModelGuids)) {
            for (String modelGuid : addModelGuids) {
                SysRoleModel sysRoleModel = new SysRoleModel();
                sysRoleModel.setModelGuid(modelGuid);
                sysRoleModel.setRoleGuid(roleGuid);
                addroleModels.add(sysRoleModel);
            }
        }
        if (ObjectUtils.isNotEmpty(delModelGuids)) {
            for (String modelGuid : delModelGuids) {
                SysRoleModel sysRoleModel = new SysRoleModel();
                sysRoleModel.setModelGuid(modelGuid);
                sysRoleModel.setRoleGuid(roleGuid);
                delroleModels.add(sysRoleModel);
            }
        }
        return sysService.updateRoleModels(addroleModels, delroleModels);
    }

    //@RequestMapping("addRoleMenus")
    //public Integer addRoleMenus(@RequestBody Map<String, Object> jsonMap) {
    //    List<String> menuGuids = MapUtils.getStrList("menuGuids", jsonMap);
    //    List<SysRoleMenu> roleModels = new ArrayList<>();
    //    String roleGuid = MapUtils.getString("roleGuid", jsonMap);
    //    for (String menuGuid : menuGuids) {
    //        SysRoleMenu sysRoleMenu = new SysRoleMenu();
    //        sysRoleMenu.setMenuGuid(menuGuid);
    //        sysRoleMenu.setRoleGuid(roleGuid);
    //        roleModels.add(sysRoleMenu);
    //    }
    //    return sysService.addRoleMenus(roleModels);
    //}

    @RequestMapping("updateUserRoles")
    public Integer updateUserRoles(@RequestBody Map<String, Object> jsonMap) {
        List<String> delRoleGuids = MapUtils.getStrList("delRoleGuids", jsonMap);
        List<String> addRoleGuids = MapUtils.getStrList("addRoleGuids", jsonMap);
        List<SysUserRole> delUserRoles = new ArrayList<>();
        List<SysUserRole> addUserRoles = new ArrayList<>();
        String userGuid = MapUtils.getString("userGuid", jsonMap);
        if (ObjectUtils.isNotEmpty(addRoleGuids)) {
            for (String roleGuid : addRoleGuids) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserGuid(userGuid);
                sysUserRole.setRoleGuid(roleGuid);
                addUserRoles.add(sysUserRole);
            }
        }
        if (ObjectUtils.isNotEmpty(delRoleGuids)) {
            for (String roleGuid : delRoleGuids) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserGuid(userGuid);
                sysUserRole.setRoleGuid(roleGuid);
                delUserRoles.add(sysUserRole);
            }
        }
        return sysService.updateUserRoles(addUserRoles, delUserRoles);
    }

    @RequestMapping("getRolesByUserGuid")
    public SysUserDTO getRolesByUserGuid(String userGuid) {
        SysUserDTO sysUserDTO = new SysUserDTO();
        sysUserDTO.setGuid(userGuid);
        return sysService.setRolesByUserGuid(sysUserDTO);
    }

    @RequestMapping("getRolesLikeRoleName")
    public List<SysRole> getRolesLikeRoleName(String roleName) {
        return sysService.getRolesLikeRoleName(roleName);
    }

    @RequestMapping("addUserConditions")
    public Integer addUserConditions(@RequestBody Map<String, Object> jsonMap) {
        List<SysUserCondition> sysUserConditionArrayList = new ArrayList<>();
        SysUserCondition sysUserCondition = new SysUserCondition();
        sysUserCondition.setLineUuid(MapUtils.getString("lineUuid", jsonMap));
        sysUserCondition.setUserGuid(MapUtils.getString("userGuid", jsonMap));
        sysUserCondition.setLineValue(MapUtils.getInteger("lineValue", jsonMap));
        sysUserConditionArrayList.add(sysUserCondition);
        return sysService.addUserConditions(sysUserConditionArrayList);
    }

    @RequestMapping("updateUserConditions")
    public Integer updateUserConditions(@RequestBody Map<String, Object> jsonMap) {
        SysUserCondition sysUserCondition = new SysUserCondition();
        sysUserCondition.setLineUuid(MapUtils.getString("lineUuid", jsonMap));
        sysUserCondition.setUserGuid(MapUtils.getString("userGuid", jsonMap));
        sysUserCondition.setLineValue(MapUtils.getInteger("lineValue", jsonMap));
        return sysService.updateUserConditions(sysUserCondition);
    }
    //
    //
    //@GetMapping("getGuid")
    //public Object getGuid() {
    //    return IdUtil.simpleUUID();
    //}

    @RequestMapping("getSelectByUser")
    public Object getSelectByUser(@RequestBody Map<String, Object> jsonMap) {
        //获取用户的下拉
        return currentDao.getSelectByUser(MapUtils.getString("userGuid", jsonMap));
    }

}
