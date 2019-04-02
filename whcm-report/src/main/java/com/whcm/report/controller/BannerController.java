package com.whcm.report.controller;

import java.util.List;
import java.util.UUID;

import com.ruoyi.common.utils.file.qiniu.QiniuUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.whcm.report.domain.Banner;
import com.whcm.report.service.IBannerService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.web.multipart.MultipartFile;

/**
 * 轮播图 信息操作处理
 * 
 * @author dong.chao
 * @date 2019-03-28
 */
@Controller
@RequestMapping("/report/banner")
public class BannerController extends BaseController
{
    private String prefix = "report/banner";
	
	@Autowired
	private IBannerService bannerService;

	@RequiresPermissions("report:banner:view")
	@GetMapping()
	public String banner()
	{
	    return prefix + "/banner";
	}
	
	/**
	 * 查询轮播图列表
	 */
	@RequiresPermissions("report:banner:list")
	@PostMapping("/list")
	@ResponseBody
	public TableDataInfo list(Banner banner)
	{
		startPage();
        List<Banner> list = bannerService.selectBannerList(banner);
		return getDataTable(list);
	}
	
	
	/**
	 * 导出轮播图列表
	 */
	@RequiresPermissions("report:banner:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(Banner banner)
    {
    	List<Banner> list = bannerService.selectBannerList(banner);
        ExcelUtil<Banner> util = new ExcelUtil<Banner>(Banner.class);
        return util.exportExcel(list, "banner");
    }
	
	/**      -
	 * 新增轮播图
	 */
	@GetMapping("/add")
	public String add()
	{
	    return prefix + "/add";
	}
	
	/**
	 * 新增保存轮播图
	 */
	@RequiresPermissions("report:banner:add")
	@Log(title = "轮播图", businessType = BusinessType.INSERT)
	@PostMapping("/add")
	@ResponseBody
	public AjaxResult addSave(MultipartFile file, Banner banner)
	{
		try {
			// 上传文件名
			String fileName = file.getOriginalFilename();
			int suffixIndex = fileName.lastIndexOf(".");
			String suffix = fileName.substring(suffixIndex);
			// 随机取文件名
			String name = "banner/"+UUID.randomUUID().toString()+suffix;
			//上传至七牛云
			String bannerUrl = QiniuUtils.updateFile(file,name);
			//存入数据库
			banner.setBannerUrl(bannerUrl);
			return toAjax(bannerService.insertBanner(banner));
		}catch (Exception e){
			logger.info("save banner error:{}",e.getMessage());
		}

		return  null;
	}

	/**
	 * 修改轮播图
	 */
	@GetMapping("/edit/{bannerId}")
	public String edit(@PathVariable("bannerId") Integer bannerId, ModelMap mmap)
	{
		Banner banner = bannerService.selectBannerById(bannerId);
		mmap.put("banner", banner);
	    return prefix + "/edit";
	}
	
	/**
	 * 修改保存轮播图
	 */
	@RequiresPermissions("report:banner:edit")
	@Log(title = "轮播图", businessType = BusinessType.UPDATE)
	@PostMapping("/edit")
	@ResponseBody
	public AjaxResult editSave(Banner banner)
	{		
		return toAjax(bannerService.updateBanner(banner));
	}
	
	/**
	 * 删除轮播图
	 */
	@RequiresPermissions("report:banner:remove")
	@Log(title = "轮播图", businessType = BusinessType.DELETE)
	@PostMapping( "/remove")
	@ResponseBody
	public AjaxResult remove(String ids)
	{		
		return toAjax(bannerService.deleteBannerByIds(ids));
	}
	
}
