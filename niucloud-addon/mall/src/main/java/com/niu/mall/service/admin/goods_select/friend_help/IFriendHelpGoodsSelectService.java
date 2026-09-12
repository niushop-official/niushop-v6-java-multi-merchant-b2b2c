package com.niu.mall.service.admin.goods_select.friend_help;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.admin.goods.vo.SelectGoodsListVo;
import com.niu.mall.service.admin.goods_select.friend_help.param.FriendHelpGoodsSelectParam;

public interface IFriendHelpGoodsSelectService {

    PageResult<SelectGoodsListVo> getGoodsList(PageParam pageParam, FriendHelpGoodsSelectParam param);
}
