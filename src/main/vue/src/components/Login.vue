<template>
  <div class="col-md-12">
    <div class="card card-container">
      <img
        id="profile-img"
        src="//ssl.gstatic.com/accounts/ui/avatar_2x.png"
        class="profile-img-card"
      />
      <div class="form">
        <Form @submit="handleLogin" :validation-schema="schema">
            <div class="form-group">
                <label for="username">Username</label>
                <Field name="username" type="text" class="form-control" />
                <ErrorMessage name="username" class="error-feedback" />
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <Field name="password" type="password" class="form-control" />
                <ErrorMessage name="password" class="error-feedback" />
            </div>

            <div class="form-group-bottom-right">
                    <button class="login-button">Login</button>
            </div>

            <div class="form-group">
                <div v-if="message" class="alert alert-danger" role="alert">
                    {{ message }}
                </div>
            </div>
        </Form>
      </div>
    </div>
  </div>
</template>

<script>
import { Form, Field, ErrorMessage } from "vee-validate";
import * as yup from "yup";
import AuthService from "@/services/auth.service";

export default {
  name: "Login",
  components: {
    Form,
    Field,
    ErrorMessage,
  },
  data() {
    const schema = yup.object().shape({
      username: yup.string().required("Username is required!"),
      password: yup.string().required("Password is required!"),
    });

    return {
      loading: false,
      message: "",
      schema,
    };
  },
  computed: {
    loggedIn() {
      return this.store.state.auth.status.loggedIn;
    },
  },
  created() {
    if (this.loggedIn) {
      this.router.push("/profile");
    }
  },
  methods: {
    async handleLogin(user) {
      this.loading = true;
      console.log("in Handle login");
      const response = await AuthService.login(user);
      if (response.status === 200) {
          await this.router.push("/login")
      }

    },
  },
};
</script>

<style scoped>
label {
  display: block;
  margin-top: 10px;
}

.card-container.card {
  max-width: 350px !important;
  padding: 40px 40px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.form-group-bottom-right {
    @extends form-group;
    justify-content: flex-end;
    display: flex;
    padding-top : 10px
}

.card {
  background-color: #f7f7f7;
  padding: 20px 25px 30px;
  margin: 0 auto 25px;
  margin-top: 50px;
  -moz-border-radius: 2px;
  -webkit-border-radius: 2px;
  border-radius: 2px;
  -moz-box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
  -webkit-box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
  box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
}

.profile-img-card {
  width: 96px;
  height: 96px;
  margin: 0 auto 10px;
  display: block;
  -moz-border-radius: 50%;
  -webkit-border-radius: 50%;
  border-radius: 50%;
}

.error-feedback {
  color: red;
}

.login-button {
    @extend is-clickable.card;
}

.form {
    padding-left: 20px;
}
</style>
