import axios, { AxiosResponse } from "axios";

const API_URL = "http://localhost:8080/start/";

class AuthService {
  async login(user: { username: string; password: string }): Promise<any> {
    return axios
      .post(API_URL + "login", {
        username: user.username,
        password: user.password,
      })
      .then((response: AxiosResponse) => {
        if (response.data.accessToken) {
          localStorage.setItem("user", JSON.stringify(response.data));
        }

        return response.data;
      });
  }

  logout(): void {
    localStorage.removeItem("user");
  }
}

const authService = new AuthService();
export default authService;
