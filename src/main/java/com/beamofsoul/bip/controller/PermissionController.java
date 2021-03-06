package com.beamofsoul.bip.controller;

import static com.beamofsoul.bip.management.util.JSONUtils.newInstance;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.beamofsoul.bip.entity.Permission;
import com.beamofsoul.bip.management.mvc.Attribute;
import com.beamofsoul.bip.management.mvc.ConditionAttribute;
import com.beamofsoul.bip.management.mvc.IdAttribute;
import com.beamofsoul.bip.management.mvc.PageableAttribute;
import com.beamofsoul.bip.management.security.Authorize;
import com.beamofsoul.bip.management.security.CustomPermissionEvaluator;
import com.beamofsoul.bip.management.util.CommonConvertUtils;
import com.beamofsoul.bip.service.PermissionService;

@Controller
@RequestMapping("/admin/permission")
public class PermissionController extends BaseAbstractController {

	@Resource
	private PermissionService permissionService;
	
	@Resource
	private CustomPermissionEvaluator customPermissionEvaluator;
	
	@Authorize("permission:list")
	@RequestMapping(value = "/adminList")
	public String adminList() {
		return "/permission/admin_permission_list";
	}
	
	@Authorize("permission:add")
	@RequestMapping(value = "/singleAdd", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject addSingle(@RequestBody Permission permission) {
		return newInstance("created",permissionService.create(permission));
	}
	
	@Authorize("permission:list")
	@RequestMapping(value = "permissionsByPage", method = RequestMethod.POST, produces = PRODUCES_APPLICATION_JSON)
	@ResponseBody
	public JSONObject getPageableData(@RequestBody Map<String, Object> map,
			@PageableAttribute Pageable pageable, @ConditionAttribute Object condition) {
		return newInstance(permissionService.findAll(pageable, permissionService.onSearch((JSONObject) condition)));
	}

	@Authorize("permission:list")
	@RequestMapping(value = "children", method = RequestMethod.POST, produces = PRODUCES_APPLICATION_JSON)
	@ResponseBody
	public JSONObject getChildrenData(@RequestBody Map<String, Object> map, @ConditionAttribute Object condition) {
		return newInstance("children",permissionService.findRelationalAll(permissionService.onRelationalSearch((JSONObject) condition)));
	}
	
	@RequestMapping(value = "allAvailable", method = RequestMethod.GET, produces = PRODUCES_APPLICATION_JSON)
	@ResponseBody
	public JSONObject getAllAvailableData() {
		return newInstance("all",permissionService.findAllAvailableData());
	}
	
	@RequestMapping(value = "single", method = RequestMethod.POST, produces = PRODUCES_APPLICATION_JSON)
	@ResponseBody
	public JSONObject getSingleJSONObject(@RequestBody Map<String, Object> map, @IdAttribute Long id) {
		return newInstance("obj",permissionService.findById(id));
	}
	
	@Authorize("permission:update")
	@RequestMapping(value = "singleUpdate", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject updateSingle(@RequestBody Permission permission) {
		return newInstance("updated",permissionService.update(permission));
	}
	
	@Authorize("permission:delete")
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@ResponseBody
	public JSONObject delete(@RequestBody String ids) {
		return newInstance("count",permissionService
				.delete(CommonConvertUtils.convertToLongArray(ids)));
	}
	
	@RequestMapping(value = "/checkPermissionNameUnique", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject checkPermissionNameUnique(@RequestBody Map<String, Object> map, @IdAttribute Long id, @Attribute("data") String permissionName) {
		return newInstance("isUnique", permissionService.checkPermissionNameUnique(permissionName, id));
	}
	
	@RequestMapping(value = "/isUsedPermissions", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject isUsedPermissions(@RequestBody String ids) {
		return newInstance("isUsed", permissionService.isUsedPermissions(CommonConvertUtils.convertToLongArray(ids)));
	}
	
	@RequestMapping(value = "/hasPermission", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject hasPermission(@RequestBody Object action) {
		return newInstance("isPermissive", customPermissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(), null, action));
	}
}
