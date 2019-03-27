<!DOCTYPE html>
<html>

<head>
	<meta charset="utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7; IE=EDGE">
	<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
	<title>产品详情页</title>
	 <link rel="icon" href="assets/img/favicon.ico">

    <link rel="stylesheet" type="text/css" href="css/webbase.css" />
    <link rel="stylesheet" type="text/css" href="css/pages-item.css" />
    <link rel="stylesheet" type="text/css" href="css/pages-zoom.css" />
    <link rel="stylesheet" type="text/css" href="css/widget-cartPanelView.css" />

	<#--?eval是json字符串转对象-->
	<#assign images = goodsDesc.itemImages?eval>
	<#assign customAttributeItems = goodsDesc.customAttributeItems?eval>
	<#assign specificationItems = goodsDesc.specificationItems?eval>

    <script type="text/javascript" src="plugins/angularjs/angular.min.js">  </script>
    <script type="text/javascript" src="js/base.js">  </script>
    <script type="text/javascript" src="js/controller/itemController.js">  </script>

    <script type="text/javascript">
        var skuList = [
			<#list itemList as item>
				{	"id":"${item.id?c}",
                    "title":"${item.title!''}",
                    "price":"${item.price?c}",
                    "spec":${item.spec}
                },
			</#list>
        ]
    </script>

</head>

<body ng-app="pinyougou" ng-controller="itemController" ng-init="loadSku()">
	<#include "head.ftl">

	<!--页面顶部结束-->
	<div class="py-container">
		<div id="item">
			<div class="crumb-wrap">
				<ul class="sui-breadcrumb">
					<li>
						<a href="#">${category1}</a>
					</li>
					<li>
						<a href="#">${category2}</a>
					</li>
					<li>
						<a href="#">${category3}</a>
					</li>
				</ul>
			</div>
			<!--product-info-->
			<div class="product-info">
				<div class="fl preview-wrap">
					<!--放大镜效果-->
					<div class="zoom">
						<!--默认第一个预览大图-->
						<div id="preview" class="spec-preview">
							<span class="jqzoom"><img jqimg="${images[0].url}" src="${images[0].url}" /></span>
						</div>

						<div class="spec-scroll">
							<a class="prev">&lt;</a>
							<!--左右按钮-->
							<div class="items">
								<ul>
                                    <!--下方的缩略图-->
									<#list images as image>
                                        <li><img src="${image.url}" bimg="${image.url}" onmousemove="preview(this)" /></li>
									</#list>
								</ul>
							</div>
							<a class="next">&gt;</a>
						</div>
					</div>
				</div>
				<div class="fr itemInfo-wrap">
					<div class="sku-name">
						<h4>{{sku.title}}</h4>
					</div>
					<div class="news"><span>${goods.caption}</span></div>
					<div class="summary">
						<div class="summary-wrap">
							<div class="fl title">
								<i>价　　格</i>
							</div>
							<div class="fl price">
								<i>¥</i>
								<em>{{sku.price}}</em>
								<span>降价通知</span>
							</div>
							<div class="fr remark">
								<i>累计评价</i><em>612188</em>
							</div>
						</div>
						<div class="summary-wrap">
							<div class="fl title">
								<i>促　　销</i>
							</div>
							<div class="fl fix-width">
								<i class="red-bg">加价购</i>
								<em class="t-gray">满999.00另加20.00元，或满1999.00另加30.00元，或满2999.00另加40.00元，即可在购物车换
购热销商品</em>
							</div>
						</div>
					</div>
					<div class="support">
						<div class="summary-wrap">
							<div class="fl title">
								<i>支　　持</i>
							</div>
							<div class="fl fix-width">
								<em class="t-gray">以旧换新，闲置手机回收  4G套餐超值抢  礼品购</em>
							</div>
						</div>
						<div class="summary-wrap">
							<div class="fl title">
								<i>配 送 至</i>
							</div>
							<div class="fl fix-width">
								<em class="t-gray">满999.00另加20.00元，或满1999.00另加30.00元，或满2999.00另加40.00元，即可在购物车换购热销商品</em>
							</div>
						</div>
					</div>
					<div class="clearfix choose">
						<div id="specification" class="summary-wrap clearfix">
							<#list specificationItems as spec>
								<dl>
									<dt>
										<div class="fl title">
										<i>${spec.attributeName}</i>
									</div>
									</dt>
									<#list spec.attributeValue as attr>
										<dd><a ng-click="selectSpec('${spec.attributeName}','${attr}')" class="{{isSelectSpec('${spec.attributeName}','${attr}')?'selected':''}}">${attr}<span title="点击取消选择">&nbsp;</span></a></dd>
									</#list>
								</dl>
							</#list>
						</div>

						
						<div class="summary-wrap">
							<div class="fl title">
								<div class="control-group">
									<div class="controls">
										<input ng-model="num" autocomplete="off" type="text" value="1" minnum="1" class="itxt" />
										<a ng-click="addNum(1)" href="javascript:void(0)" class="increment plus">+</a>
										<a ng-click="addNum(-1)" href="javascript:void(0)" class="increment mins">-</a>
									</div>
								</div>
							</div>
							<div class="fl">
								<ul class="btn-choose unstyled">
									<li>
										<a href="cart.html" target="_blank" class="sui-btn  btn-danger addshopcar">加入购物车</a>
									</li>
								</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!--product-detail-->
			<#include "foot.ftl">


<!--页面底部结束-->
</body>

</html>