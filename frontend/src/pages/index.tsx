import { PageContainer, ProCard, ProForm, ProFormSelect, ProFormText } from '@ant-design/pro-components';
import { message, Typography } from 'antd';
import dayjs from 'dayjs';
import React, { useMemo, useState } from 'react';
import { makeBooking } from '../services/api';

const { Paragraph } = Typography;

const BASE_URL = process.env.BASE_URL || 'http://localhost:8080';

const slots = [
  { value: '09:00-10:00', label: '09:00 - 10:00' },
  { value: '10:00-11:00', label: '10:00 - 11:00' },
  { value: '14:00-15:00', label: '14:00 - 15:00' },
  { value: '15:00-16:00', label: '15:00 - 16:00' },
];

export default function IndexPage() {
  const [loading, setLoading] = useState(false);
  const availableDates = useMemo(() => {
    const days: string[] = [];
    for (let i = 0; i < 21; i += 1) {
      days.push(dayjs().add(i, 'day').format('YYYY-MM-DD'));
    }
    return days;
  }, []);

  return (
    <PageContainer>
      <ProCard title="入职预约" bordered>
        <Paragraph>
          选择您的微信 openId、预约日期和时间段完成入职办理预约。系统会在办理前 24 小时通过小程序消息提醒。
        </Paragraph>
        <ProForm
          grid
          submitter={{ searchConfig: { submitText: '提交预约' } }}
          onFinish={async (values) => {
            try {
              setLoading(true);
              const resp = await makeBooking(BASE_URL, {
                openId: values.openId,
                date: values.date,
                slot: values.slot,
              });
              message.success(`预约成功，编号 ${resp.id}，排队顺序 ${resp.position}，状态 ${resp.status}`);
            } catch (err: any) {
              message.error(err.message || '预约失败');
            } finally {
              setLoading(false);
            }
          }}
        >
          <ProFormText name="openId" label="微信 openId" rules={[{ required: true }]} />
          <ProFormSelect
            name="date"
            label="预约日期"
            rules={[{ required: true }]}
            options={availableDates.map((d) => ({ label: d, value: d }))}
          />
          <ProFormSelect name="slot" label="预约时段" rules={[{ required: true }]} options={slots} />
        </ProForm>
        {loading && <Paragraph>提交中...</Paragraph>}
      </ProCard>
    </PageContainer>
  );
}
