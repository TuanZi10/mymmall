package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by Flash on 2018/3/19.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService{
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    ICategoryService iCategoryService;

    public ServerResponse saveOrUpdateProduct(Product product){
        if (product != null){
            if (StringUtils.isEmpty(product.getSubImages())){
                String[] imgArray = product.getSubImages().split(",");
                if (imgArray.length > 0){
                    product.setMainImage(imgArray[0]);
                }
            }

            //判断产品id是否存在
            if (product.getId() != null){
                int count = productMapper.updateByPrimaryKey(product);
                if (count > 0)
                    return ServerResponse.createBySuccessMsg("成功更新产品!");
                return ServerResponse.createByErrorMsg("更新产品失败！");
            }else {
                int rowcount = productMapper.insert(product);
                if (rowcount > 0)
                    return ServerResponse.createBySuccessMsg("新增产品成功!");
                return ServerResponse.createByErrorMsg("新增产品失败");
            }
        }
        return ServerResponse.createByErrorMsg("新增或更新产品错误！");
    }

    public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
        if (productId == null || status == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //更新产品信息
        Product p = new Product();
        p.setId(productId);
        p.setStatus(status);
        int rowcount = productMapper.updateByPrimaryKeySelective(p);
        if (rowcount > 0){
            return ServerResponse.createBySuccessMsg("更新产品销售状态成功！");
        }
        return ServerResponse.createByErrorMsg("更新产品销售状态失败！");
    }

    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if (productId == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            ServerResponse.createByErrorMsg("产品已下架或不存在！");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize){
        //填充分页查询逻辑
        PageHelper.startPage(pageNum,pageSize);
        List<Product> products = productMapper.selectList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : products){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(products);//分页结果
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }


    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setId(product.getId());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setName(product.getName());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtile(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetails(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        //获取配置文件
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        //如果没有则设置为根节点
        if (category == null){
            productDetailVo.setCategoryId(0);
        }else {
            productDetailVo.setCategoryId(category.getParentId());
        }
        productDetailVo.setCreateTime(DateTimeUtil.date2Str(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.date2Str(product.getUpdateTime()));

        return productDetailVo;
    }


    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(productName))
            productName = new StringBuffer().append("%").append(productName).append("%").toString();
        List<Product> productList = productMapper.selectByNameAndProductId(productName,productId);
        //再次转换成listVo
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);//分页结果
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

}
