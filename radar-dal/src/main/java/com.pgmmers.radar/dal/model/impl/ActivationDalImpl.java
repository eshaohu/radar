package com.pgmmers.radar.dal.model.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.pgmmers.radar.dal.bean.ActivationQuery;
import com.pgmmers.radar.dal.bean.PageResult;
import com.pgmmers.radar.dal.model.ActivationDal;
import com.pgmmers.radar.mapper.ActivationMapper;
import com.pgmmers.radar.model.ActivationPO;
import com.pgmmers.radar.util.BaseUtils;
import com.pgmmers.radar.vo.model.ActivationVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import tk.mybatis.mapper.entity.Example;


@Service
public class ActivationDalImpl implements ActivationDal {

	public static Logger logger = LoggerFactory.getLogger(ActivationDalImpl.class);

	@Autowired
	private ActivationMapper activationMapper;

	@Override
	public ActivationVO get(Long id) {
		ActivationPO activation = activationMapper.selectByPrimaryKey(id);
		if (activation != null) {
			ActivationVO activationVO = new ActivationVO();
			BeanUtils.copyProperties(activation, activationVO);
			return activationVO;
		}
		return null;
	}

	@Override
	public PageResult<ActivationVO> query(ActivationQuery query) {
		PageHelper.startPage(query.getPageNo(), query.getPageSize());

		Example example = new Example(ActivationPO.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("modelId", query.getModelId());
		if (!StringUtils.isEmpty(query.getName())) {
			criteria.andLike("activationName", BaseUtils.buildLike(query.getName()));
		}
		if (query.getStatus() != null) {
		    criteria.andEqualTo("status", query.getStatus());
		}
		List<ActivationPO> list = activationMapper.selectByExample(example);
		Page<ActivationPO> page = (Page<ActivationPO>) list;

		List<ActivationVO> listVO = new ArrayList<>();
		for (ActivationPO activationPO : page.getResult()) {
			ActivationVO activationVO = new ActivationVO();
			BeanUtils.copyProperties(activationPO, activationVO);
			listVO.add(activationVO);
		}

		PageResult<ActivationVO> pageResult = new PageResult<ActivationVO>(page.getPageNum(), page.getPageSize(),
				(int) page.getTotal(), listVO);
		return pageResult;
	}

	@Override
	public int save(ActivationVO activation) {
		ActivationPO activationPO = new ActivationPO();
		BeanUtils.copyProperties(activation, activationPO);
		Date sysDate = new Date();
		int count = 0;
		if (activation.getId() == null) {
			activationPO.setCreateTime(sysDate);
			activationPO.setUpdateTime(sysDate);
			count = activationMapper.insertSelective(activationPO);
			activation.setId(activationPO.getId());// 返回id
		} else {
			activationPO.setUpdateTime(sysDate);
			count = activationMapper.updateByPrimaryKeySelective(activationPO);
		}
		return count;
	}

	@Override
	public int delete(Long[] id) {
		Example example = new Example(ActivationPO.class);
		example.createCriteria().andIn("id", Arrays.asList(id));
		int count = activationMapper.deleteByExample(example);
		// TODO 删除关联子表
		return count;
	}

}
