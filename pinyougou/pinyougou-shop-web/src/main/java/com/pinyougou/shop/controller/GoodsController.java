package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/goods")
@RestController
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return goodsService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            //获取添加商品的商家名字(登陆时的名字,也是数据库表格中的主键)
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getGoods().setSellerId(name);
            goods.getGoods().setAuditStatus("0");
            goodsService.addGoods(goods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    @GetMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findGoodsById(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            TbGoods oldSeller = goodsService.findOne(goods.getGoods().getId());
            if(!sellerId.equals(oldSeller.getSellerId())||!sellerId.equals(goods.getGoods().getSellerId())){
                return Result.fail("非法操作");
            }
            goodsService.updateGoods(goods);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.deleteGoodsByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    @GetMapping("/putAway")
    public Result putAway(Long[] ids) {
        try {
            int i = goodsService.putAway(ids);
            if(i==0){
                return Result.fail("上架失败,有商品未通过审核");
            }else{
                return Result.ok("上架成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("上架失败");
    }

    @GetMapping("/soldOut")
    public Result soldOut(Long[] ids) {
        try {
            int i = goodsService.soldOut(ids);
            if(i==0){
                return Result.fail("下架失败,有商品未通过审核或者还没上架");
            }else{
                return Result.ok("下架成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("下架失败");
    }

    /**
     * 分页查询列表
     * @param goods 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbGoods goods, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.search(page, rows, goods);
    }

    @GetMapping("/updateStatus")
    public Result updateStatus(Long[] ids,String status){
        try {
            goodsService.updateStatus(ids,status);
            return Result.ok("提交成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
           return Result.fail("提交失败");
    }

}
