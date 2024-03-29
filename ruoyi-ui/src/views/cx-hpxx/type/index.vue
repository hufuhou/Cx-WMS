<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="108px">
      <el-form-item label="货品类型编码" prop="gtCode">
        <el-input
          v-model="queryParams.gtCode"
          placeholder="请输入货品类型编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="货品类型名称" prop="gtName">
        <el-input
          v-model="queryParams.gtName"
          placeholder="请输入货品类型名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="货品状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择货品状态" clearable>
          <el-option
            v-for="dict in dict.type.goods_states"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="操作人" prop="createBy">
        <el-input
          v-model="queryParams.createBy"
          placeholder="请输入操作人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作时间" prop="createTime">
        <el-date-picker clearable
                        v-model="queryParams.createTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="选择操作时间">
        </el-date-picker>
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
          v-hasPermi="['cx-hpxx:type:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-sort"
          size="mini"
          @click="toggleExpandAll"
        >展开/折叠
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="typeList"
      row-key="gtId"
      :default-expand-all="isExpandAll"
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
    >
      <el-table-column label="货品类型编码" prop="gtCode" width="228px"/>
      <el-table-column label="货品类型名称" align="center" prop="gtName"/>
      <el-table-column label="排序" align="center" prop="sort"/>
      <el-table-column label="货品状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.goods_states" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark"/>
      <el-table-column label="父级类型" align="center" prop="parentId"/>
      <el-table-column label="操作人" align="center" prop="createBy"/>
      <el-table-column label="操作时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['cx-hpxx:type:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-plus"
            @click="handleAdd(scope.row)"
            v-hasPermi="['cx-hpxx:type:add']"
          >新增
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['cx-hpxx:type:remove']"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改货品类型对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="货品类型编码" prop="gtCode">
          <el-input v-model="form.gtCode" placeholder="自动获取系统编码" :disabled="true"/>
        </el-form-item>
        <el-form-item label="父级类型" prop="parentId">
          <treeselect v-model="form.parentId" :options="typeOptions" :normalizer="normalizer"
                      placeholder="请选择父级类型"/>
        </el-form-item>
        <el-form-item label="货品类型名称" prop="gtName">
          <el-input v-model="form.gtName" placeholder="请输入货品类型名称"/>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="form.sort" placeholder="请输入排序"/>
        </el-form-item>
        <el-form-item label="货品状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.goods_states"
              :key="dict.value"
              :label="parseInt(dict.value)"
            >{{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {listType, getType, delType, addType, updateType, listGoods} from "@/api/cx-hpxx/type";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";

// 检查货品类型是否存在关联的货品信息
function checkGoodsExist(gtId) {
  return new Promise((resolve, reject) => {
    // 根据货品类型ID查询相关的货品信息
    listGoods(gtId)
      .then(response => {
       const goodsList = response.rows.filter(goods => goods.gtId === gtId);
        const exists = goodsList.length > 0;
        resolve({ exists, goodsList });
      })
      .catch(error => {
        console.error(error);
        reject(error);
      });
  });
}

export default {
  name: "Type",
  dicts: ['goods_states'],
  components: {
    Treeselect
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 货品类型表格数据
      typeList: [],
      // 货品类型树选项
      typeOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否展开，默认全部展开
      isExpandAll: true,
      // 重新渲染表格状态
      refreshTable: true,
      // 查询参数
      queryParams: {
        gtCode: null,
        gtName: null,
        status: null,
        createBy: null,
        createTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        gtName: [
          {required: true, message: "货品类型名称不能为空", trigger: "blur"}
        ],
        sort: [
          {required: true, message: "排序不能为空", trigger: "blur"}
        ],
        status: [
          {required: true, message: "货品状态不能为空", trigger: "change"}
        ],
        createBy: [
          {required: true, message: "操作人不能为空", trigger: "blur"}
        ],
        createTime: [
          {required: true, message: "操作时间不能为空", trigger: "blur"}
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询货品类型列表 */
    getList() {
      this.loading = true;
      listType(this.queryParams).then(response => {
        this.typeList = this.handleTree(response.data, "gtId", "parentId");
        this.loading = false;
      });
    },
    /** 转换货品类型数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children;
      }
      return {
        id: node.gtId,
        label: node.gtName,
        children: node.children
      };
    },
    /** 查询货品类型下拉树结构 */
    getTreeselect() {
      listType().then(response => {
        this.typeOptions = [];
        const data = {gtId: 0, gtName: '顶级节点', children: []};
        data.children = this.handleTree(response.data, "gtId", "parentId");
        this.typeOptions.push(data);
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
        gtId: null,
        gtCode: null,
        gtName: null,
        sort: null,
        status: null,
        remark: null,
        parentId: null,
        createBy: null,
        createTime: null,
        isDelte: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd(row) {
      this.reset();
      this.getTreeselect();
      if (row != null && row.gtId) {
        this.form.parentId = row.gtId;
      } else {
        this.form.parentId = 0;
      }
      this.open = true;
      this.title = "添加货品类型";
    },
    /** 展开/折叠操作 */
    toggleExpandAll() {
      this.refreshTable = false;
      this.isExpandAll = !this.isExpandAll;
      this.$nextTick(() => {
        this.refreshTable = true;
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.getTreeselect();
      if (row != null) {
        this.form.parentId = row.gtId;
      }
      getType(row.gtId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改货品类型";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.gtId != null) {
            updateType(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addType(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    // handleDelete(row) {
    //   this.$modal.confirm('是否确认删除货品类型编号为"' + row.gtId + '"的数据项？').then(function() {
    //     return delType(row.gtId);
    //   }).then(() => {
    //     this.getList();
    //     this.$modal.msgSuccess("删除成功");
    //   }).catch(() => {});
    // }
    handleDelete(row) {
      // 先检查货品类型是否有关联的货品信息
      checkGoodsExist(row.gtId)
        .then(response => {
          if (response.exists) {
            // 货品类型有关联的货品信息，提示用户先删除货品
            this.$modal.alert('该货品类型下存在货品信息，请先删除相关货品。');
          } else {
            // 货品类型没有关联的货品信息，确认删除操作
            this.$modal.confirm('是否确认删除货品类型为"' + row.gtName + '"的数据项？')
              .then(() => {
                return delType(row.gtId);
              })
              .then(() => {
                this.getList();
                this.$modal.msgSuccess('删除成功');
              })
              .catch(() => {
              });
          }
        })
        .catch(error => {
          console.error(error);
        });
    },
  }
};
</script>
