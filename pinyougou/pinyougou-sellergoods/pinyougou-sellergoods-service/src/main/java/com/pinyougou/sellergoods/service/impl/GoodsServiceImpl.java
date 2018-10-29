package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.content.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
@Transactional
@Service(interfaceClass = GoodsService.class )
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private TypeTemplateMapper typeTemplateMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private SellerMapper sellerMapper;
    @Autowired
    private ItemMapper itemMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbGoods goods) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        //不查询已删除的商品
        criteria.andNotEqualTo("isDelete","1");
        criteria.andEqualTo("sellerId",goods.getSellerId());
        if(!StringUtils.isEmpty(goods.getSellerId())){
            criteria.andEqualTo("sellerId",goods.getSellerId());
        }
        if(!StringUtils.isEmpty(goods.getAuditStatus())){
            criteria.andEqualTo("auditStatus",goods.getAuditStatus());
        }
        if(!StringUtils.isEmpty(goods.getGoodsName())){
            criteria.andLike("goodsName","%"+goods.getGoodsName()+"%");
        }
        List<TbGoods> list = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void addGoods(Goods goods) {
        //添加商品基本信息
        add(goods.getGoods());
        int i =1/0;
        //由于商品描述表的id和商品基本信息表的id一致,所以需要设置描述表的id
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        //添加商品的描述信息
        goodsDescMapper.insertSelective(goods.getGoodsDesc());
        //添加商品列表
        saveItemList(goods);
}

    @Override
    public void updateGoods(Goods goods) {
        //更新goods商品基本描述表
        goods.getGoods().setAuditStatus("0");
        goodsMapper.updateByPrimaryKeySelective(goods.getGoods());
       //更新goodsdesc表
        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());
        //删除items原来的SKU列表
        TbItem param = new TbItem();
        param.setGoodsId(goods.getGoods().getId());
        itemMapper.delete(param);
        //更新items表
        saveItemList(goods);
    }

    @Override
    public Goods findGoodsById(Long id) {
        Goods goods = new Goods();
        //根据id获取在goods表对应的的信息,并封装
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);
        //根据id获取在goodsdesc表对应的信息,并封装
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);
        //获取item表对应的信息,并封装
        TbItem tbItem = new TbItem();
        tbItem.setGoodsId(id);
        List<TbItem> itemList = itemMapper.select(tbItem);
        goods.setItemList(itemList);
        return goods;
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        TbGoods tbGoods = new TbGoods();
        tbGoods.setAuditStatus(status);
        //将id数组转成集合
        List<Long> idsList = Arrays.asList(ids);
        //创建查询条件对象
        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",idsList);
        //更新提交审核商品的状态
        goodsMapper.updateByExampleSelective(tbGoods,example);
        //如果前端传过来的status是2,代表审核通过,要修改sku表的status状态
        if("2".equals(status)){
            TbItem tbItem = new TbItem();
            tbItem.setStatus("1");
            //创建查询条件对象
            Example itemExample = new Example(TbItem.class);
            itemExample.createCriteria().andIn("goodsId",idsList);
            //修改sku表的status状态
            itemMapper.updateByExampleSelective(tbItem,itemExample);
        }

    }

    @Override
    public void deleteGoodsByIds(Long[] ids) {
        TbGoods tbGoods = new TbGoods();
        tbGoods.setIsDelete("1");
        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(tbGoods,example);

    }

    @Override
    public int putAway(Long[] ids) {
        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        List<TbGoods> tbGoodsList = goodsMapper.selectByExample(example);
        for (TbGoods tbGood : tbGoodsList) {
            if(!"2".equals(tbGood.getAuditStatus())){
                return 0;
            }
        }
        TbGoods tbGoods = new TbGoods();
        tbGoods.setIsMarketable("1");
        goodsMapper.updateByExampleSelective(tbGoods,example);
        return 1;
    }

    @Override
    public int soldOut(Long[] ids) {
        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        List<TbGoods> tbGoodsList = goodsMapper.selectByExample(example);
        for (TbGoods tbGood : tbGoodsList) {
            if(!"2".equals(tbGood.getAuditStatus())||!"1".equals(tbGood.getIsMarketable())){
                return 0;
            }
        }
        TbGoods tbGoods = new TbGoods();
        tbGoods.setIsMarketable("0");
        goodsMapper.updateByExampleSelective(tbGoods,example);
        return 1;
    }

    private void saveItemList(Goods goods) {
        if("1".equals(goods.getGoods().getIsEnableSpec())){
            //获取前端传过来的商品SKU列表
            List<TbItem> itemList = goods.getItemList();
            for (TbItem tbItem : itemList) {
                //组合规格选项形成SKU标题
                String title = goods.getGoods().getGoodsName();
                Map<String ,Object> map = JSON.parseObject(tbItem.getSpec());
                Set<Map.Entry<String, Object>> entries = map.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    title += "" +entry.getValue().toString();
                }
                tbItem.setTitle(title);
                setItemValues(tbItem,goods);
                itemMapper.insertSelective(tbItem);
            }
        }else {
            TbItem tbItem = new TbItem();
            tbItem.setTitle(goods.getGoods().getGoodsName());
            tbItem.setPrice(goods.getGoods().getPrice());
            tbItem.setNum(9999);
            tbItem.setStatus("0");
            tbItem.setSpec("{}");
            setItemValues(tbItem,goods);
            itemMapper.insertSelective(tbItem);
        }
    }

    public void setItemValues(TbItem tbItem,Goods goods){
        //封装品牌
        Long brandId = goods.getGoods().getBrandId();
        TbBrand tbBrand = brandMapper.selectByPrimaryKey(brandId);
        tbItem.setBrand(tbBrand.getName());
        //封装商品分类
        Long category3Id = goods.getGoods().getCategory3Id();
        TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(category3Id);
        tbItem.setCategory(tbItemCat.getName());
        //封装商品分类id
        tbItem.setCategoryid(category3Id);
        //封装商品的图片,把商品的第一张图片作为SKU图
        String itemImages = goods.getGoodsDesc().getItemImages();
        //上面拿到的值是一个字符串数组,里面存的时一个个的json对象,如:[{"color":"黑色","url":"http://img11.360buyimg.com/n1/s450x450_jfs/t3076/42/8593902551/206108/fdb1a60f/58c60fc3Nf9faa2fa.jpg"},{"color":"金色","url":"http://img11.360buyimg.com/n1/s450x450_jfs/t3076/42/8593902551/206108/fdb1a60f/58c60fc3Nf9faa2fa.jpg"}]
        List<Map> imgList = JSONArray.parseArray(itemImages, Map.class);
        if(imgList!=null&&imgList.size()>0) {
            tbItem.setImage(imgList.get(0).get("url").toString());
        }
        //封装商品的id
        tbItem.setGoodsId(goods.getGoods().getId());
        //封装商家的id
        tbItem.setSellerId(goods.getGoods().getSellerId());
        //封装商家的名称
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        tbItem.setSeller(seller.getName());
        //封装商品创建时间
        tbItem.setCreateTime(new Date());
        //封装商品更新时间
        tbItem.setUpdateTime(tbItem.getCreateTime());
    }
}
