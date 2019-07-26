package com.voucher.manage2.controller;

import cn.hutool.core.util.IdUtil;
import com.voucher.manage2.constant.ResultConstant;
import com.voucher.manage2.dto.MenuDTO;
import com.voucher.manage2.exception.BaseException;
import com.voucher.manage2.service.MenuService;
import com.voucher.manage2.service.SysService;
import com.voucher.manage2.tkmapper.entity.SysRoleRouter;
import com.voucher.manage2.utils.CommonUtils;
import com.voucher.manage2.utils.MapUtils;
import com.voucher.manage2.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wr
 * @description 菜单控制器
 * @date 2019/5/23
 */
@RestController
@RequestMapping("menu")
@Slf4j
public class MenuController {

    @Autowired
    private MenuService menuService;
    @Autowired
    private SysService sysService;

    @RequestMapping(value = "/selectFileMenu")
    public Object selectFileMenu(String[] roomGuids, String rootGuid) {
        if (ObjectUtils.isEmpty(rootGuid)) {
            return ResultConstant.FAILED;
        }
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setGuid(rootGuid);
        return menuService.selectFileMenu(menuDTO, roomGuids);
    }

    @RequestMapping(value = "/selectAllRootMenu")
    public List<MenuDTO> selectAllRootMenu() {
        return menuService.selectAllRootMenu();
    }

    @RequestMapping(value = "/insertFileMenu")
    public Integer insertMenu(@RequestBody Map<String, Object> jsonMap) {
        //log.debug("insertMenu===" + jsonMap);
        MenuDTO menu = new MenuDTO();
        menu.setGuid(IdUtil.simpleUUID());
        menu.setLevel(MapUtils.getInteger("level", jsonMap));
        menu.setRootGuid(MapUtils.getString("rootGuid", jsonMap));
        menu.setName(MapUtils.getString("name", jsonMap));
        menu.setParentGuid(MapUtils.getString("parentGuid", jsonMap));
        menu.setRequired(MapUtils.getBoolean("required", jsonMap));
        return menuService.insertMenu(menu);
    }

    @GetMapping(value = "/insertRootMenu")
    public Integer insertRootMenu(String name) {
        MenuDTO menu = new MenuDTO();
        String rootGuid = IdUtil.simpleUUID();
        menu.setGuid(rootGuid);
        menu.setLevel(0);
        menu.setRootGuid(rootGuid);
        menu.setName(name);
        menu.setParentGuid(CommonUtils.getCurrentUserGuid());
        return menuService.insertMenu(menu);
    }


    @RequestMapping(value = "/delLeafMenu")
    public Integer delLeafMenu(@RequestBody Map<String, Object> jsonMap) {

        List<String> leafMenus = (List<String>) jsonMap.get("leafMenus");
        if (ObjectUtils.isEmpty(leafMenus)) {
            throw BaseException.getDefault();
        }
        return menuService.delLeafMenu(leafMenus);
    }

    @RequestMapping(value = "/updateMenu")
    public Integer updateMenu(@RequestBody Map<String, Object> jsonMap) {
        //log.debug("insertMenu===" + jsonMap);
        return menuService.updateMenu(jsonMap);
    }

    @RequestMapping("getRoleRouters")
    public Object getRoutersByRootGuid(String rootGuid, String roleGuid) {
        return sysService.getRoutersByRootGuid(rootGuid, roleGuid);
    }

    @RequestMapping("getUserRouters")
    public Object getRoutersByRootGuidAndRoleGuid(String rootGuid) {
        if (CommonUtils.isSuperAdmin()) {
            //管理员返回所有的菜单
            return sysService.getRoutersByRootGuid(rootGuid, null);
        }
        return sysService.getRoutersByUserGuid(rootGuid);
    }

    @RequestMapping("addRoleRouters")
    public Integer addRoleRouters(@RequestBody Map<String, Object> jsonMap) {
        String roleGuid = MapUtils.getString("roleGuid", jsonMap);
        List<String> routers = MapUtils.getStrList("routers", jsonMap);
        List<SysRoleRouter> sysRouters = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(roleGuid, routers)) {
            sysRouters = routers.stream().map(routerGuid -> {
                SysRoleRouter sysRoleRouter = new SysRoleRouter();
                sysRoleRouter.setGuid(IdUtil.simpleUUID());
                sysRoleRouter.setRoleGuid(roleGuid);
                sysRoleRouter.setRouterGuid(routerGuid);
                return sysRoleRouter;
            }).collect(Collectors.toList());
        }
        return sysService.addRoleRouters(sysRouters);
    }

}
