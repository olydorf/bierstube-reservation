
import authHeader from "./auth-header";
import axios from "axios";
import type {AxiosResponse} from "axios";


const API_URL = "http://localhost:8080/api/test/";

class UserService {
  getPublicContent(): Promise<AxiosResponse> {
    return axios.get(API_URL + "all");
  }

  getUserBoard(): Promise<AxiosResponse> {
    return axios.get(API_URL + "user", { headers: authHeader() });
  }

  getModeratorBoard(): Promise<AxiosResponse> {
    return axios.get(API_URL + "mod", { headers: authHeader() });
  }

  getAdminBoard(): Promise<AxiosResponse> {
    return axios.get(API_URL + "admin", { headers: authHeader() });
  }
}

const userService = new UserService();
export default userService;
