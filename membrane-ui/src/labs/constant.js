/**
 * @description 项目中所用到的下拉框数据
 */
let selectObj = {
      authStatus: [{
          value: null,
          label: '全部'
        },
        {
          value: 0,
          label: '未认证'
        },
        {
          value: 1,
          label: '已认证',
          children: [{
            value: 0,
            label: 'CAS'
            },
            {
              value: 1,
              label: 'OAUTH'
            },
            {
              value: 2,
              label: 'SAML'
            }
          ]
        }
      ], //应用管理-认证状态
      validMode: [{
          modeId: null,
          modeName: '全部'
        },
        {
          modeId: 0,
          modeName: '安全模式'
        },
        {
          modeId: 1,
          modeName: '性能模式'
        }
      ], //应用管理-验证方式
      functionType: [{
          fId: 1,
          fName: '查询类'
        },
        {
          fId: 0,
          fName: '编辑类'
        }
      ], //应用管理-功能类型
}

export default selectObj;

