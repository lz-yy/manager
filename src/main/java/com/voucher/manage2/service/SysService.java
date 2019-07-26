package com.voucher.manage2.service;

import com.voucher.manage2.dto.SysRouterDTO;
import com.voucher.manage2.dto.SysModelDTO;
import com.voucher.manage2.dto.SysRoleDTO;
import com.voucher.manage2.dto.SysUserDTO;
import com.voucher.manage2.tkmapper.entity.*;

import java.util.List;

public interface SysService {

    Integer addUrls(List<SysUrl> urls);

    Integer addModel(SysModel model);

    Integer updateModelUrls(List<SysModelUrl> modelUrls, List<SysModelUrl> delModelUrls);

    Integer addRole(SysRole role);

    Integer updateRoleModels(List<SysRoleModel> roleModels, List<SysRoleModel> delModelGuids);

    //Integer addRoleMenus(List<SysRoleMenu> roleMenus);

    Integer updateUserRoles(List<SysUserRole> userRoles, List<SysUserRole> delUserRoles);

    Integer addUserConditions(List<SysUserCondition> userCondition);

    List<SysUrl> getUrlsLikeCondition(String urlCondition);

    SysModelDTO getUrlsByModel(String modelGuid);

    List<SysModel> getModelsLikeModelName(String modelName);

    SysRoleDTO getModelsByRoleGuid(String RoleGuid);

    List<SysRole> getRolesLikeRoleName(String roleName);

    SysUserDTO setRolesByUserGuid(SysUserDTO sysUserDTO);

    List<SysUserCondition> getUserConditionsByUserGuid(String guid);

    Integer updateUserConditions(SysUserCondition sysUserCondition);

    List<SysRouter> getRouterByUser(String userGuid);

    SysRouterDTO getRoutersByRootGuid(String rootGuid, String roleGuid);

    Integer addRoleRouters(List<SysRoleRouter> sysRouters);

    SysRouterDTO getRoutersByUserGuid(String rootGuid);
}
