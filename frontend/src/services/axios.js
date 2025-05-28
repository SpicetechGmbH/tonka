import axios from 'axios';

export default axios;

export const baseUrl = import.meta.env.VITE_API_URL;

export const dtsAxios = axios.create({baseURL: baseUrl})
