export interface SlotTemplate {
  startHour: number;
  endHour: number;
  capacity: number;
}

export interface BookingPayload {
  openId: string;
  date: string;
  slot: string;
}

export interface BookingResponse {
  id: string;
  position: number;
  status: string;
}

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const resp = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(options?.headers || {}),
    },
  });
  if (!resp.ok) {
    const text = await resp.text();
    throw new Error(text || resp.statusText);
  }
  return resp.json();
}

export function getTemplates(baseUrl: string) {
  return request<SlotTemplate[]>(`${baseUrl}/admin/slots`);
}

export function saveTemplate(baseUrl: string, payload: SlotTemplate[]) {
  return request(`${baseUrl}/admin/slots`, {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export function makeBooking(baseUrl: string, payload: BookingPayload) {
  return request<BookingResponse>(`${baseUrl}/public/book`, {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export function checkIn(baseUrl: string, openId: string, timestamp: string) {
  return request(`${baseUrl}/public/check-in`, {
    method: 'POST',
    body: JSON.stringify({ openId, timestamp }),
  });
}

export function getQueue(baseUrl: string) {
  return request(`${baseUrl}/admin/queue`);
}

export function callNext(baseUrl: string) {
  return request(`${baseUrl}/admin/queue/call`, { method: 'POST' });
}

export function markMissed(baseUrl: string, id: string) {
  return request(`${baseUrl}/admin/queue/${id}/miss`, { method: 'POST' });
}

export function sendReminder(baseUrl: string, id: string) {
  return request(`${baseUrl}/admin/queue/${id}/remind`, { method: 'POST' });
}

export function getStats(baseUrl: string) {
  return request(`${baseUrl}/admin/stats`);
}
