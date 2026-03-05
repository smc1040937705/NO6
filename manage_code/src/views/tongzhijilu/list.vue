<template>
	<div>
		<div class="center_view">
			<div class="list_search_view">
				<el-form :model="searchQuery" class="search_form" >
					<div class="search_view">
						<div class="search_label">
							用户账号：
						</div>
						<div class="search_box">
							<el-input class="search_inp" v-model="searchQuery.zhanghao" placeholder="用户账号"
								clearable>
							</el-input>
						</div>
					</div>
					<div class="search_view">
						<div class="search_label">
							发送状态：
						</div>
						<div class="search_box">
							<el-select
								class="search_sel"
								clearable
								v-model="searchQuery.fasongzhuangtai"
								placeholder="发送状态"
								>
								<el-option label="待发送" value="0"></el-option>
								<el-option label="发送成功" value="1"></el-option>
								<el-option label="发送失败" value="2"></el-option>
							</el-select>
						</div>
					</div>
					<div class="search_view">
						<div class="search_label">
							通知类型：
						</div>
						<div class="search_box">
							<el-select
								class="search_sel"
								clearable
								v-model="searchQuery.tongzhileixing"
								placeholder="通知类型"
								>
								<el-option label="预约成功通知" :value="1"></el-option>
								<el-option label="就诊前提醒" :value="2"></el-option>
								<el-option label="就诊当天提醒" :value="3"></el-option>
							</el-select>
						</div>
					</div>
					<div class="search_btn_view">
						<el-button class="search_btn" type="primary" @click="searchClick()" size="small">搜索</el-button>
						<el-button class="reset_btn" type="info" @click="resetClick()" size="small">重置</el-button>
					</div>
				</el-form>
				<div class="btn_view">
					<el-button class="del_btn" type="danger" :disabled="selRows.length?false:true" @click="delClick(null)"  v-if="btnAuth('tongzhijilu','删除')">
						<i class="iconfont icon-shanchu4"></i>
						删除
					</el-button>
					<el-button class="retry_btn" type="warning" :disabled="selRows.length?false:true" @click="retryBatchClick()" v-if="btnAuth('tongzhijilu','重试')">
						<i class="iconfont icon-refresh"></i>
						批量重试
					</el-button>
				</div>
			</div>
			<el-table
				v-loading="listLoading"
				border
				:stripe='false'
				@selection-change="handleSelectionChange"
				ref="table"
				v-if="btnAuth('tongzhijilu','查看')"
				:data="list"
				@row-click="listChange">
				<el-table-column :resizable='true' align="left" header-align="left" type="selection" width="55" />
				<el-table-column label="序号" width="70" :resizable='true' align="left" header-align="left">
					<template #default="scope">{{ (listQuery.page-1)*listQuery.limit+scope.$index + 1}}</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="tongzhibianhao"
					label="通知编号">
					<template #default="scope">
						{{scope.row.tongzhibianhao}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="yuyuebianhao"
					label="预约编号">
					<template #default="scope">
						{{scope.row.yuyuebianhao}}
					</template>
				</el-table-column>
				<el-table-column min-width="120"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="yishengzhanghao"
					label="医生账号">
					<template #default="scope">
						{{scope.row.yishengzhanghao}}
					</template>
				</el-table-column>
				<el-table-column min-width="120"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="zhanghao"
					label="用户账号">
					<template #default="scope">
						{{scope.row.zhanghao}}
					</template>
				</el-table-column>
				<el-table-column min-width="120"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="tongzhileixing"
					label="通知类型">
					<template #default="scope">
						<el-tag v-if="scope.row.tongzhileixing==1" type="success">预约成功通知</el-tag>
						<el-tag v-else-if="scope.row.tongzhileixing==2" type="warning">就诊前提醒</el-tag>
						<el-tag v-else-if="scope.row.tongzhileixing==3" type="primary">就诊当天提醒</el-tag>
						<el-tag v-else>未知</el-tag>
					</template>
				</el-table-column>
				<el-table-column min-width="200"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="tongzhineirong"
					label="通知内容"
					show-overflow-tooltip>
					<template #default="scope">
						{{scope.row.tongzhineirong}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="jihuafasongshijian"
					label="计划发送时间">
					<template #default="scope">
						{{scope.row.jihuafasongshijian}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="shijifasongshijian"
					label="实际发送时间">
					<template #default="scope">
						{{scope.row.shijifasongshijian}}
					</template>
				</el-table-column>
				<el-table-column min-width="100"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="fasongzhuangtai"
					label="发送状态">
					<template #default="scope">
						<el-tag v-if="scope.row.fasongzhuangtai=='0'" type="info">待发送</el-tag>
						<el-tag v-else-if="scope.row.fasongzhuangtai=='1'" type="success">发送成功</el-tag>
						<el-tag v-else-if="scope.row.fasongzhuangtai=='2'" type="danger">发送失败</el-tag>
						<el-tag v-else>未知</el-tag>
					</template>
				</el-table-column>
				<el-table-column min-width="100"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="jieshouzhuangtai"
					label="接收状态">
					<template #default="scope">
						<el-tag v-if="scope.row.jieshouzhuangtai=='0'" type="info">未接收</el-tag>
						<el-tag v-else-if="scope.row.jieshouzhuangtai=='1'" type="warning">已接收</el-tag>
						<el-tag v-else-if="scope.row.jieshouzhuangtai=='2'" type="success">已读</el-tag>
						<el-tag v-else>未知</el-tag>
					</template>
				</el-table-column>
				<el-table-column min-width="80"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="chongshicishu"
					label="重试次数">
					<template #default="scope">
						{{scope.row.chongshicishu}}
					</template>
				</el-table-column>
				<el-table-column label="操作" width="200" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">
						<el-button class="view_btn" type="info" v-if=" btnAuth('tongzhijilu','查看')" @click="infoClick(scope.row.id)">
							<i class="iconfont icon-sousuo2"></i>
							查看
						</el-button>
						<el-button class="del_btn" type="danger" @click="delClick(scope.row.id)"  v-if="btnAuth('tongzhijilu','删除')">
							<i class="iconfont icon-shanchu4"></i>
							删除
						</el-button>
						<el-button class="retry_btn" type="warning" v-if="scope.row.fasongzhuangtai=='2' && btnAuth('tongzhijilu','重试')" @click="retryClick(scope.row.id)">
							<i class="iconfont icon-refresh"></i>
							重试
						</el-button>
					</template>
				</el-table-column>
			</el-table>
			<el-pagination
				background
				:layout="layouts.join(',')"
				:total="total"
				:page-size="listQuery.limit"
                v-model:current-page="listQuery.page"
				prev-text="上一页"
				next-text="下一页"
				:hide-on-single-page="false"
				:style='{}'
				:page-sizes="[10, 20, 30, 40, 50, 100]"
				@size-change="sizeChange"
				@current-change="currentChange"  />
		</div>
	</div>
</template>
<script setup>
	import axios from 'axios'
    import moment from "moment"
	import {
		reactive,
		ref,
		getCurrentInstance,
		nextTick,
		onMounted,
		watch,
		computed,
		inject
	} from 'vue'
	import {
		useRoute,
		useRouter
	} from 'vue-router'
	import {
		ElMessageBox
	} from 'element-plus'
	import {
		useStore
	} from 'vuex';
	const store = useStore()
	const user = computed(()=>store.getters['user/session'])
	const avatar = ref(store.state.user.avatar)
	const context = getCurrentInstance()?.appContext.config.globalProperties;

	const tableName = 'tongzhijilu'
	const formName = '通知记录'
	const route = useRoute()

	onMounted(()=>{
	})

	const list = ref(null)
	const table = ref(null)
	const listQuery = ref({
		page: 1,
		limit: 10,
		sort: 'id',
		order: 'desc'
	})
	const searchQuery = ref({})
	const selRows = ref([])
	const listLoading = ref(false)
	const listChange = (row) =>{
		nextTick(()=>{
			table.value.toggleRowSelection(row)
		})
	}

	const getList = () => {
		listLoading.value = true
		let params = JSON.parse(JSON.stringify(listQuery.value))
		params['sort'] = 'id'
		params['order'] = 'desc'
		if(searchQuery.value.zhanghao && searchQuery.value.zhanghao!=''){
			params['zhanghao'] = '%' + searchQuery.value.zhanghao + '%'
		}
		if(searchQuery.value.fasongzhuangtai && searchQuery.value.fasongzhuangtai!=''){
			params['fasongzhuangtai'] = searchQuery.value.fasongzhuangtai
		}
		if(searchQuery.value.tongzhileixing && searchQuery.value.tongzhileixing!=''){
			params['tongzhileixing'] = searchQuery.value.tongzhileixing
		}
		context.$http({
			url: `${tableName}/page`,
			method: 'get',
			params: params
		}).then(res => {
			listLoading.value = false
			list.value = res.data.data.list
			total.value = Number(res.data.data.total)
		})
	}

	const delClick = (id) => {
		let ids = ref([])
		if (id) {
			ids.value = [id]
		} else {
			if (selRows.value.length) {
				for (let x in selRows.value) {
					ids.value.push(selRows.value[x].id)
				}
			} else {
				return false
			}
		}
		ElMessageBox.confirm(`是否删除选中${formName}`, '提示', {
			confirmButtonText: '是',
			cancelButtonText: '否',
			type: 'warning',
		}).then(() => {
			context.$http({
				url: `${tableName}/delete`,
				method: 'post',
				data: ids.value
			}).then(res => {
				context?.$toolUtil.message('删除成功', 'success',()=>{
					getList()
				})
			})
		}).catch(_ => {})
	}

	const handleSelectionChange = (e) => {
		selRows.value = e
	}

	const total = ref(0)
	const layouts = ref(["total","prev","pager","next","sizes","jumper"])
	const sizeChange = (size) => {
		listQuery.value.limit = size
		getList()
	}
	const currentChange = (page) => {
		listQuery.value.page = page
		getList()
	}

	const btnAuth = (e,a)=>{
		return context?.$toolUtil.isAuth(e,a)
	}

	const searchClick = () => {
		listQuery.value.page = 1
		getList()
	}

	const resetClick = () => {
		searchQuery.value = {}
		getList()
	}

	const infoClick = (id=null)=>{
		if(id){
			// 查看详情
			context.$http({
				url: `${tableName}/info/${id}`,
				method: 'get'
			}).then(res => {
				// 可以在这里打开详情弹窗
				ElMessageBox.alert(res.data.data.tongzhineirong, '通知内容详情', {
					confirmButtonText: '确定'
				})
			})
		}
	}

	// 单条重试
	const retryClick = (id) => {
		ElMessageBox.confirm('确定要重试发送该通知吗？', '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		}).then(() => {
			context.$http({
				url: `${tableName}/retry/${id}`,
				method: 'post'
			}).then(res => {
				context?.$toolUtil.message(res.data.msg || '重试请求已提交', 'success')
				getList()
			})
		}).catch(_ => {})
	}

	// 批量重试
	const retryBatchClick = () => {
		if (!selRows.value.length) {
			context?.$toolUtil.message('请选择要重试的记录', 'warning')
			return
		}
		let ids = []
		for (let x in selRows.value) {
			if (selRows.value[x].fasongzhuangtai == '2') {
				ids.push(selRows.value[x].id)
			}
		}
		if (ids.length === 0) {
			context?.$toolUtil.message('选中的记录中没有发送失败的', 'warning')
			return
		}
		ElMessageBox.confirm(`确定要批量重试 ${ids.length} 条发送失败的通知吗？`, '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		}).then(() => {
			context.$http({
				url: `${tableName}/retryBatch`,
				method: 'post',
				data: ids
			}).then(res => {
				context?.$toolUtil.message(res.data.msg || '批量重试请求已提交', 'success')
				getList()
			})
		}).catch(_ => {})
	}

	const init = () => {
		getList()
	}
	init()
</script>
<style lang="scss" scoped>

	.list_search_view {
		.search_form {
			.search_view {
				.search_label {
				}
				.search_box {
					:deep(.search_inp) {
					}
					:deep(.search_sel) {
						.select-trigger{
							height: 100%;
							.el-input{
								height: 100%;
							}
						}
					}
				}
			}
			.search_btn_view {
				.search_btn {
				}
				.reset_btn {
					margin-left: 10px;
				}
			}
		}
		.btn_view {
			:deep(.el-button--default){
			}
			:deep(.el-button--danger){
			}
			:deep(.el-button--warning){
			}
		}
	}

	.el-table {
		:deep(.el-table__header-wrapper) {
			thead {
				tr {
					th {
						.cell {
						}
					}
				}
			}
		}
		:deep(.el-table__body-wrapper) {
			tbody {
				tr {
					td {
						.cell {
							.el-button--info {
							}
							.el-button--danger {
							}
							.el-button--warning {
							}
						}
					}
				}
			}
		}
	}

	.el-pagination {
		:deep(.el-pagination__total) {
		}
		:deep(.btn-prev) {
		}
		:deep(.btn-next) {
		}
		:deep(.btn-prev:disabled) {
		}
		:deep(.btn-next:disabled) {
		}
		:deep(.el-pager) {
			.number {
			}
			.number:hover {
			}
			.number.is-active {
			}
		}
		:deep(.el-pagination__sizes) {
			display: inline-block;
			vertical-align: top;
			font-size: 13px;
			line-height: 28px;
			height: 28px;
			.el-select {
			}
		}
		:deep(.el-pagination__jump) {
			.el-input {
			}
		}
	}
</style>
