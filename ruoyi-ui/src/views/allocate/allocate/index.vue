<template>
  <div class="app-container">
    <el-form>
      <el-form-item>
        <el-row>
          <el-button plain @click="getInfoByDate('today')">今 日</el-button>
          <el-button type="primary" plain @click="getInfoByDate('yesterday')">昨 日</el-button>
          <el-button type="success" plain @click="getInfoByDate('thisWeek')">本 周</el-button>
          <el-button type="info" plain @click="getInfoByDate('lastWeek')">上 周</el-button>
          <el-button type="warning" plain @click="getInfoByDate('thisMonth')">本 月</el-button>
          <el-button type="danger" plain @click="getInfoByDate('lastMonth')">上 月</el-button>
        </el-row>
      </el-form-item>
    </el-form>
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="调拨单号" prop="tdCode">
        <el-input
          v-model="queryParams.tdCode"
          placeholder="请输入调拨单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请日" prop="tDate">
        <el-date-picker clearable
                        v-model="queryParams.tDate"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择调拨申请日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="调出仓库" prop="outWId">
        <el-input
          v-model="queryParams.outWId"
          placeholder="请输入调出仓库编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="调入仓库" prop="inWId">
        <el-input
          v-model="queryParams.inWId"
          placeholder="请输入调入仓库编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['allocate:allocate:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['allocate:allocate:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['allocate:allocate:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['allocate:allocate:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="allocateList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
<!--      <el-table-column label="调拨编码" align="center" prop="tId"/>-->
      <el-table-column label="调拨单号" align="center" prop="tdCode"/>
      <el-table-column label="单据状态" align="center" prop="docStatus">
        <template slot-scope="scope">
          <span v-if="scope.row.docStatus === '1'">待审核</span>
          <span v-else-if="scope.row.docStatus === '2'">驳回</span>
          <span v-else-if="scope.row.docStatus === '3'">已完成</span>
          <span v-else>未知状态</span>
        </template>
      </el-table-column>
      <el-table-column label="调拨申请日期" align="center" prop="tDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.tDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="调出仓库" align="center" prop="outWId">
        <template slot-scope="scope">
          <span v-if="scope.row.outWId === '1'">玲露仓库</span>
          <span v-else-if="scope.row.outWId === '2'">鸿衡仓库</span>
          <span v-else-if="scope.row.outWId === '3'">易腾仓库</span>
          <span v-else-if="scope.row.outWId === '4'">懒财仓库</span>
          <span v-else>未知状态</span>
        </template>
      </el-table-column>
      <el-table-column label="调入仓库" align="center" prop="inWId">
        <template slot-scope="scope">
          <span v-if="scope.row.inWId === '1'">玲露仓库</span>
          <span v-else-if="scope.row.inWId === '2'">鸿衡仓库</span>
          <span v-else-if="scope.row.inWId === '3'">易腾仓库</span>
          <span v-else-if="scope.row.inWId === '4'">懒财仓库</span>
          <span v-else>未知状态</span>
        </template>
      </el-table-column>
      <el-table-column label="出库状态" align="center" prop="outStatus">
        <template slot-scope="scope">
          <span v-if="scope.row.outStatus === 1">已出库</span>
          <span v-else-if="scope.row.outStatus === 2">部分出库</span>
          <span v-else-if="scope.row.outStatus === 3">已出库</span>
          <span v-else>未知状态</span>
        </template>
      </el-table-column>
<!--      <el-table-column label="数据字典" align="center" prop="tType"/>-->
      <el-table-column label="备注" align="center" prop="remark">
        <template slot-scope="scope">
          {{ scope.row.remark === null ||  scope.row.remark === " " ? '暂无备注' : scope.row.remark }}
        </template>
      </el-table-column>
      <el-table-column label="入库状态" align="center" prop="inStatus">
        <template slot-scope="scope">
          <span v-if="scope.row.inStatus === 0">待审核</span>
          <span v-else-if="scope.row.inStatus === 1">驳回</span>
          <span v-else-if="scope.row.inStatus === 2">未入库</span>
          <span v-else-if="scope.row.inStatus === 3">部分入库</span>
          <span v-else-if="scope.row.inStatus === 4">已完成</span>
          <span v-else>未知状态</span>
        </template>
      </el-table-column>
      <el-table-column label="调拨负责人" align="center" prop="manager"/>
<!--      <el-table-column label="是否存在" align="center" prop="isDelte"/>-->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['allocate:allocate:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['allocate:allocate:remove']"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改调拨明细对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <h1> 绝赞开发中! </h1>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" disabled="disabled">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {addAllocate, delAllocate, getAllocate, listAllocate, updateAllocate} from "@/api/allocate/allocate";
import {parseTime} from "@/utils/ruoyi";
import {selectDataParam} from "@/api/opdm/opdm";

export default {
  name: "Allocate",
  data() {
    return {
      //今日 or 昨日
      TodayOrYesterday: "",
      //周,月开始日
      BeginDay: "",
      //周,月结束日
      EndDay: "",
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 调拨明细表格数据
      allocateList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        tdCode: null,
        docStatus: null,
        tDate: null,
        outWId: null,
        inWId: null,
        outStatus: null,
        tType: null,
        inStatus: null,
        tManager: null,
        isDelte: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        tdCode: [
          {required: true, message: "调拨单号不能为空", trigger: "blur"}
        ],
        docStatus: [
          {required: true, message: "单据状态不能为空", trigger: "change"}
        ],
        tDate: [
          {required: true, message: "调拨申请日期不能为空", trigger: "blur"}
        ],
        outWId: [
          {required: true, message: "调出仓库 ID不能为空", trigger: "blur"}
        ],
        inWId: [
          {required: true, message: "调入仓库不能为空", trigger: "blur"}
        ],
        outStatus: [
          {required: true, message: "出库状态不能为空", trigger: "change"}
        ],
        tType: [
          {required: true, message: "数据字典不能为空", trigger: "change"}
        ],
        inStatus: [
          {required: true, message: "入库状态不能为空", trigger: "change"}
        ],
        tManager: [
          {required: true, message: "外键，关联用户表不能为空", trigger: "blur"}
        ],
        createBy: [
          {required: true, message: "关联至用户表不能为空", trigger: "blur"}
        ],
        createTime: [
          {required: true, message: "创建时间不能为空", trigger: "blur"}
        ],
        updateBy: [
          {required: true, message: "关联至用户表不能为空", trigger: "blur"}
        ],
        updateTime: [
          {required: true, message: "修改时间不能为空", trigger: "blur"}
        ],
        isDelte: [
          {required: true, message: "0：存在；1：已删除，不存在不能为空", trigger: "blur"}
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {

    /**
     * 获取时间参数
     * @param param
     */
    async getDateParam(param) {
      return new Promise((resolve, reject) => {
        selectDataParam(param).then(response => {
          if (typeof response.data === 'string') {
            // 如果是字符串
            this.TodayOrYesterday = response.data;
            resolve(true);
            this.$modal.msgSuccess("查询" + this.TodayOrYesterday + "数据");
          } else if (Array.isArray(response.data)) {
            // 如果是数组
            this.BeginDay = response.data[0];
            this.EndDay = response.data[1];
            this.$modal.msgSuccess("查询起始日: " + this.BeginDay + ",结束日: " + this.EndDay + "间数据!");
            resolve(true);
          } else {
            // 处理其他类型的数据，或者抛出错误提示
            console.error('非法数据类型:', typeof response.data);
            reject(false);
          }
        }).catch(error => {
          console.error('获取参数失败:', error);
          reject(false);
        });
      });
    },

    /**
     * 按时间段获取信息
     * @param param
     */
    async getInfoByDate(param) {
      let DateParams;
      this.TodayOrYesterday = null;
      this.BeginDay = null;
      this.EndDay = null;
      DateParams = null;
      try {
        // 等待 getDateParam 方法执行完毕
        await this.getDateParam(param);
        // 在这里可以获取到正确的 DateParams
        DateParams = [this.TodayOrYesterday, this.BeginDay, this.EndDay];
        console.info(DateParams)
        //TODO : 从这下面添加业务代码
      } catch (error) {
        // 处理错误
        console.error('按时间段查询进货信息失败:', error);
      }
    },

    parseTime,
    /** 查询调拨明细列表 */
    getList() {
      this.loading = true;
      listAllocate(this.queryParams).then(response => {
        this.allocateList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        tId: null,
        tdCode: null,
        docStatus: null,
        tDate: null,
        outWId: null,
        inWId: null,
        outStatus: null,
        tType: null,
        remark: null,
        inStatus: null,
        tManager: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        isDelte: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.tId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加调拨明细";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const tId = row.tId || this.ids
      getAllocate(tId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改调拨明细";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.tId != null) {
            updateAllocate(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAllocate(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const tIds = row.tId || this.ids;
      this.$modal.confirm('是否确认删除调拨明细编号为"' + tIds + '"的数据项？').then(function () {
        return delAllocate(tIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('allocate/allocate/export', {
        ...this.queryParams
      }, `allocate_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
