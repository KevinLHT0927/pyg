package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface BrandService extends BaseService<TbBrand> {
    /**
     * 查询所有品牌
     * @return 品牌列表
     */
    public List<TbBrand> queryAll();

    /**
     *查询品牌分页
     * @param page
     * @param rows
     * @return 品牌列表
     */
    public List<TbBrand> testPage(Integer page, Integer rows);

    PageResult search(TbBrand tbBrand, Integer page, Integer rows);
}
