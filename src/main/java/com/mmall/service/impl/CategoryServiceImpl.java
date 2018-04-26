package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by Flash on 2018/3/5.
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    public ServerResponse addCategory(String categoryName,Integer parentID){
        if (StringUtils.isBlank(categoryName) || parentID == null){
            return ServerResponse.createByErrorMsg("添加商品分类错误！");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentID);
        category.setStatus(true);

        int rowcount = categoryMapper.insert(category);
        if (rowcount > 0){
            return ServerResponse.createBySuccessMsg("添加商品分类成功！");
        }
        return ServerResponse.createByErrorMsg("添加商品分类失败！");
    }

    public ServerResponse updateCategoryName(Integer categoryId,String categoryName){
        if (categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMsg("更新参数品类错误！");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int rowcount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowcount > 0){
            return ServerResponse.createBySuccessMsg("更新名字品类成功！");
        }
        return ServerResponse.createByErrorMsg("更新名字品类失败！");
    }

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到当前分类！");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归查询本节点及孩子节点id
     * @param categoryId
     * @return
     */
    public ServerResponse selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId != null){
            for (Category category : categorySet){
                categoryIdList.add(category.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    //递归算法，算出子节点
    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null){
            categorySet.add(category);
        }
        //判断是否有子节点
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        //在mybatis中会自动对结果集做空判断,会自动结束
        for (Category categoryItem : categoryList){
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }

}
