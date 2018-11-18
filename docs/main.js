let openedPage = null
function openPage(page) {
	//hide the openedPage
	//openedPage to document.getElementById(page)
	//show openedPage
	if (page == "profilePage"){
		if(!renderProfile())
			return
	}
	if (openedPage != null) {
		openedPage.hidden = true
	}
	openedPage = document.getElementById(page);
	openedPage.hidden = false
}

function loadExploreData(category) {
	if (category == ''){
		alert('category is empty')
		return
	}
	else{
		browseCategory(category).then(renderExplore)
	}
}
function renderExplore(data) {
	document.getElementById("evtList").innerHTML='';
	for(let event of data) {
		let templ = document.getElementById("exploreTempl");
		let clone = templ.content.cloneNode(true);
		clone.getElementById("evtName").textContent=event.eventName
		clone.getElementById("creatorId").textContent=event.creatorId
		clone.getElementById("spotsAvailable").textContent="Spots Open: "+(event.maxMembers-event.members.length)
		let memberList = clone.getElementById('members')
		for(let mem of event.members) {
			let li = document.createElement('li')
			li.textContent = mem;
			memberList.appendChild(
				li
			)
		}
		let userId = document.getElementById("userId").value
		let joinButton = clone.getElementById("joinButton")
		joinButton.hidden = (event.maxMembers-event.members.length) <= 0 || event.members.includes(userId)
		joinButton.onclick = () => {
			userId = document.getElementById("userId").value
			if(!userId) {
				alert("User ID is empty")
				return;
			}
			joinEvent(event.category,event.id,userId).then(alert)
		}
		document.getElementById("evtList").appendChild(clone)
	}
}
function renderProfile(){
	console.log('eet')
	let userId = document.getElementById("userId").value
	if (userId==""){
		alert('userId is empty')
		return false
	}
	userDetails(userId).then((data) => {
		let eventList = document.getElementById('userEvents')
		eventList.innerHTML=''
		for (let event of data.events){
			let li = document.createElement('li')
			li.textContent = event;
			li.onclick = () => {
				if(document.getElementById('eventDeets').hidden) {
					document.getElementById('eventDeets').hidden = false
				}
				let result = event.split('/')
				eventDetails(result[0],result[1]).then(data => {
					document.getElementById("devtName").textContent=data.eventName
					document.getElementById("dcreatorId").textContent=data.creatorId
					document.getElementById("dspotsAvailable").textContent="Spots Open: "+(data.maxMembers-data.members.length)
					let memberList = document.getElementById('dmembers')
					memberList.innerHTML = "";
					for(let mem of data.members) {
						let li = document.createElement('li')
						li.textContent = mem;
						memberList.appendChild(
							li
						)
					}
					document.getElementById("verifyButton").onclick= () => {
						verify(result[0], result[1], document.getElementById('userPhoto').files[0]).then(
							alert
						)
					}
				})
			}
			eventList.appendChild(
				li
			)	
		}
		document.getElementById('userPoints').textContent="User points: "+data.points
	})

	return true
}

function mkEvent(){
	let category = document.getElementById("createCategory")
		.options[document.getElementById("createCategory").selectedIndex].value
	let eventName = document.getElementById("createEventDescription").value
	let maxMembers = document.getElementById("createMaxMember").value
	let userId = document.getElementById("userId").value
	makeEvent(category,eventName,maxMembers,userId).then(alert)
}
let newpage;
function addPage(page) {
	newpage = document.getElementById(page)
	newpage.hidden = false
}