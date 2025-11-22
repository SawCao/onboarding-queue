import { defineConfig } from 'umi';

export default defineConfig({
  npmClient: 'npm',
  history: { type: 'hash' },
  routes: [
    { path: '/', component: 'index' },
    { path: '/admin', component: 'admin' },
  ],
  antd: {},
  model: {},
  initialState: {},
  request: {},
  access: {},
  layout: false,
  mfsu: false,
});
