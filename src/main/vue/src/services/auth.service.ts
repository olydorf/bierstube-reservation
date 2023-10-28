import axios, { AxiosResponse } from "axios";

const API_URL = "http://localhost:8080/start/";

class AuthService {
  async login(username: string, password: string): Promise<any> {
    return axios
        // todo I had to change this to fix the Bad Request responses
      .post(API_URL + "login?password=" + password + "&username=" + username )
      .then((response: AxiosResponse) => {
          // console.log(response.data)
          // console.log(response.statusText)
          localStorage.setItem("user", JSON.stringify(response.data));
        console.log("login in authserveiceeeeeeeeeee")
        return response;
      });
  }

  logout(): void {
    localStorage.removeItem("user");
  }
}

const authService = new AuthService();
export default authService;
