import request from '@/utils/request'

// 查询进货订单列表
export function listPurchase(query) {
  return request({
    url: '/order/purchase/list',
    method: 'get',
    params: query
  })
}

// 查询进货订单详细
export function getPurchase(poId) {
  return request({
    url: '/order/purchase/' + poId,
    method: 'get'
  })
}

// 新增进货订单
export function addPurchase(data) {
  return request({
    url: '/order/purchase',
    method: 'post',
    data: data
  })
}

// 修改进货订单
export function updatePurchase(data) {
  return request({
    url: '/order/purchase',
    method: 'put',
    data: data
  })
}

// 删除进货订单
export function delPurchase(poId) {
  return request({
    url: '/order/purchase/' + poId,
    method: 'delete'
  })
}

//预准备产品数据
export function preProduct(data) {
  return request({
    url: '/order/purchase/preData',
    method: 'post',
    data: data
  })
}

// 获取进货编号
export function getCode() {
  return request({
    url: '/order/purchase/getPoCode',
    method: 'get'
  })
}

// 审核状态修改
export function updateAudit(status, poIds) {
  return request({
    url: '/order/purchase/' + status + '/' + poIds,
    method: 'get'
  })
}

// //添加进货明细
// export function addOrderDetail(data){
//   return request({
//     url: '/order/purchase/add',
//     method: 'post',
//     data: JSON.stringify(data)
//   })
// }

