import {
  PageContainer,
  ProCard,
  ProForm,
  ProFormDigit,
  ProFormGroup,
  ProFormList,
  ProList,
} from '@ant-design/pro-components';
import { Button, Col, Divider, Input, message, Row, Space, Statistic, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import {
  callNext,
  getQueue,
  getStats,
  getTemplates,
  markMissed,
  saveTemplate,
  sendReminder,
  SlotTemplate,
} from '../services/api';
import { clearAdminToken, getAdminToken, setAdminToken } from '../app';

const { Paragraph } = Typography;

const BASE_URL = process.env.BASE_URL || 'http://localhost:8080';

interface QueueEntry {
  id: string;
  openId: string;
  status: string;
  position: number;
  lastUpdated: string;
}

export default function AdminPage() {
  const [adminToken, setToken] = useState<string | null>(getAdminToken());
  const [templates, setTemplates] = useState<SlotTemplate[]>([]);
  const [queue, setQueue] = useState<QueueEntry[]>([]);
  const [stats, setStats] = useState<any>({});

  const refreshAll = async () => {
    try {
      const [tpl, q, s] = await Promise.all([
        getTemplates(BASE_URL),
        getQueue(BASE_URL),
        getStats(BASE_URL),
      ]);
      setTemplates(tpl);
      setQueue(q.entries || q);
      setStats(s);
    } catch (e: any) {
      message.error(e.message || '加载失败');
    }
  };

  useEffect(() => {
    if (adminToken) {
      refreshAll();
    }
  }, [adminToken]);

  const handleSaveSlots = async (values: any) => {
    try {
      await saveTemplate(BASE_URL, values.templates);
      message.success('已保存预约时段');
      refreshAll();
    } catch (e: any) {
      message.error(e.message || '保存失败');
    }
  };

  if (!adminToken) {
    return (
      <PageContainer>
        <ProCard title="管理员登录" bordered>
          <Paragraph>输入静态 Token 进入后台管理界面。</Paragraph>
          <Space>
            <Input.Password
              placeholder="X-Admin-Token"
              onChange={(e) => setToken(e.target.value)}
            />
            <Button
              type="primary"
              onClick={() => {
                if (adminToken) {
                  setAdminToken(adminToken);
                  message.success('已设置管理 Token');
                }
              }}
            >
              设置
            </Button>
          </Space>
        </ProCard>
      </PageContainer>
    );
  }

  return (
    <PageContainer
      extra={
        <Space>
          <Button
            onClick={() => {
              clearAdminToken();
              setToken(null);
            }}
          >
            清除 Token
          </Button>
          <Button type="primary" onClick={refreshAll}>
            刷新
          </Button>
        </Space>
      }
    >
      <Row gutter={[16, 16]}>
        <Col span={24}>
          <ProCard title="预约时段配置" bordered>
            <ProForm
              grid
              initialValues={{ templates }}
              submitter={{ searchConfig: { submitText: '保存' } }}
              onFinish={handleSaveSlots}
            >
              <ProFormList
                name="templates"
                initialValue={templates.length ? templates : [{ startHour: 9, endHour: 10, capacity: 10 }]}
                creatorButtonProps={{ creatorButtonText: '添加时段' }}
              >
                {(fields, { remove }) => (
                  <>
                    {fields.map((field, index) => (
                      <ProFormGroup key={field.key} rowProps={{ gutter: 8 }}>
                        <ProFormDigit name={[field.name, 'startHour']} label="开始小时" min={0} max={23} />
                        <ProFormDigit name={[field.name, 'endHour']} label="结束小时" min={1} max={24} />
                        <ProFormDigit name={[field.name, 'capacity']} label="容量" min={1} />
                        <Button danger onClick={() => remove(index)}>
                          删除
                        </Button>
                      </ProFormGroup>
                    ))}
                  </>
                )}
              </ProFormList>
            </ProForm>
          </ProCard>
        </Col>

        <Col span={12}>
          <ProCard title="队列管理" bordered extra={<Button onClick={refreshAll}>刷新</Button>}>
            <Space>
              <Button type="primary" onClick={async () => { await callNext(BASE_URL); refreshAll(); }}>
                叫号
              </Button>
              <Button
                onClick={async () => {
                  if (queue[0]) {
                    await markMissed(BASE_URL, queue[0].id);
                    refreshAll();
                  }
                }}
              >
                过号
              </Button>
              <Button
                onClick={async () => {
                  if (queue[0]) {
                    await sendReminder(BASE_URL, queue[0].id);
                    message.success('已发送提醒');
                  }
                }}
              >
                提醒当前
              </Button>
            </Space>
            <Divider />
            <ProList
              pagination={false}
              dataSource={queue}
              metas={{
                title: {
                  dataIndex: 'openId',
                  title: '用户',
                  render: (_, row) => `${row.position} - ${row.openId}`,
                },
                description: {
                  dataIndex: 'status',
                  render: (_, row) => `状态: ${row.status} / 更新时间: ${row.lastUpdated}`,
                },
              }}
            />
          </ProCard>
        </Col>

        <Col span={12}>
          <ProCard title="统计" bordered>
            <Row gutter={16}>
              <Col span={12}>
                <Statistic title="预约总数" value={stats.totalBookings || 0} />
              </Col>
              <Col span={12}>
                <Statistic title="今日签到" value={stats.todayCheckIns || 0} />
              </Col>
            </Row>
            <Divider />
            <Paragraph>{JSON.stringify(stats)}</Paragraph>
          </ProCard>
        </Col>
      </Row>
    </PageContainer>
  );
}
