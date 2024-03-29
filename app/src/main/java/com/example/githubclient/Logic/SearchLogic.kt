package com.example.githubclient.Logic

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.example.githubclient.AdaptersAndHolders.SearchAdapter
import com.example.githubclient.Requests.GithubJsonRequest
import com.example.githubclient.Models.Repository
import org.json.JSONObject

class SearchLogic {

    companion object {

        fun  getRepositories(query: String, context: Context, queue: RequestQueue,
                            recyclerView: RecyclerView, progressBar: ProgressBar) {
            val creds = AuthorizationLogic.getSavedCreds(context)
            val request = GithubJsonRequest(
                "https://api.github.com/search/repositories?q=$query",
                Response.Listener {
                        response -> makeView(recyclerView, parseReposResponse(response), context)
                        recyclerView.visibility = View.VISIBLE
                        progressBar.visibility = ProgressBar.GONE
                },
                Response.ErrorListener {
                    Toast.makeText(context,"Ошибка запроса", Toast.LENGTH_LONG).show()
                    progressBar.visibility = ProgressBar.GONE
                },
                creds.first,
                creds.second
            )
            queue.add(request)
        }

        private fun parseReposResponse(response: JSONObject): ArrayList<Repository> {
            val list = arrayListOf<Repository>()
            if (response.getInt("total_count") == 0) {
                list.add(Repository(0, "NothingHasBeenFound", null, null,
                    null, null, null, null,
                    null))
            } else {
                val items = response.getJSONArray("items")
                for (i in 0 until items.length()) {
                    val repository = items.getJSONObject(i)
                    val id = repository.getInt("id")
                    val name = repository.getString("name")
                    val description = repository.getString("description")
                    val language: String? = if (repository.getString("language") ==
                        JSONObject.NULL) null else repository.getString("language")
                    val forksCount = repository.getInt("forks_count")
                    val stargazersCount = repository.getInt("stargazers_count")
                    val owner = repository.getJSONObject("owner")
                    val ownerLogin = owner.getString("login")
                    val ownerAvatarUrl = owner.getString("avatar_url")
                    val commitsUrl = repository.getString("commits_url").
                        split("{")[0]
                    list.add(Repository(id, name, description, language, forksCount, stargazersCount,
                        ownerLogin, ownerAvatarUrl, commitsUrl)
                    )
                }
            }
            return list
        }

        private fun makeView(recyclerView: RecyclerView,
                             list: ArrayList<Repository>, context: Context) {
            recyclerView.adapter = SearchAdapter(list)
            recyclerView.layoutManager = LinearLayoutManager(context)
        }
    }
}